package com.pedro.spring.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.pedro.spring.domain.Contact;
import com.pedro.spring.domain.Login;
import com.pedro.spring.enums.Sex;
import com.pedro.spring.request.ContactRequest;
import com.pedro.spring.request.LoginRequest;
import com.pedro.spring.request.LoginSingInRequest;
import com.pedro.spring.request.RememberPassword;
import com.pedro.spring.service.ContactService;
import com.pedro.spring.service.CookieService;
import com.pedro.spring.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
public class RequestController {

    private final String USER_AUTHENTICATED = "userAuthenticated";
    private final LoginService loginService;
    private final ContactService contactService;

    @Bean
    public BCryptPasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private void userSession(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        try {
            if (CookieService.getCookie(request, USER_AUTHENTICATED) != null) {
                Login userAuthenticated = loginService.findLoginById(UUID.fromString(CookieService.getCookie(request, USER_AUTHENTICATED)));
                session.setAttribute(USER_AUTHENTICATED, userAuthenticated);
            }
        } catch (Exception e) {
            CookieService.setCookie(response, USER_AUTHENTICATED, "", 0);
            session.invalidate();

        }
    }

    private void setMessageUser(ModelAndView mv, Login login) {
        if (login.getSex().equals(Sex.M)) {
            mv.addObject("msg", "Olá, seja bem vindo " + login.getName() + " !");
        } else {
            mv.addObject("msg", "Olá, seja bem vinda " + login.getName() + " !");
        }
    }


    private ModelAndView singInAfterUpdatePassword(LoginSingInRequest loginSingInRequest, HttpServletResponse response) throws UnsupportedEncodingException {
        int timeAuthenticated = 60 * 60 * 24;
        Login loginByUsername = loginService.findLoginByUsername(loginSingInRequest.getUsername());
        CookieService.setCookie(response, USER_AUTHENTICATED, String.valueOf(loginByUsername.getId()), timeAuthenticated);
        ModelAndView mv = new ModelAndView("redirect:/home");
        mv.addObject("success", "Senha alterada com sucesso!");
        return mv;

    }

    @GetMapping("/")
    public ModelAndView home(HttpServletRequest request, LoginSingInRequest loginSingInRequest) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        try {
            if (CookieService.getCookie(request, USER_AUTHENTICATED) != null) {
                return new ModelAndView("redirect:home");
            }
        } catch (Exception e) {
            return new ModelAndView("login/login");
        }
        return new ModelAndView("login/login");
    }

    @PostMapping("/sing-in")
    public ModelAndView singIn(@Valid LoginSingInRequest loginSingInRequest, BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response, HttpSession session, @Param("remember") String remember) throws UnsupportedEncodingException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        if (bindingResult.hasErrors()) {
            //error
            ModelAndView mv = new ModelAndView("login/login");
        } else {
            //valid
            Login loginByUsername = loginService.findLoginByUsername(loginSingInRequest.getUsername());
            if (loginByUsername != null) {
                //user is found , check password
                if (getPasswordEncoder().matches(loginSingInRequest.getPassword(), loginByUsername.getPassword())) {
                    //user authenticated
                    int timeAuthenticated = 60 * 60 * 24;
                    if (remember != null) {
                        timeAuthenticated = timeAuthenticated * 365;
                    }
                    CookieService.setCookie(response, USER_AUTHENTICATED, String.valueOf(loginByUsername.getId()), timeAuthenticated);
                    return new ModelAndView("redirect:/home");
                } else {
                    //bad credentials
                    ModelAndView mv = new ModelAndView("login/login");
                    mv.addObject("errorUser", Boolean.TRUE);
                    return mv;
                }
            } else {
                //user not found
                ModelAndView mv = new ModelAndView("login/login");
                mv.addObject("errorUser", Boolean.TRUE);
                return mv;
            }
        }
        return new ModelAndView("login/login");
    }


    @GetMapping("/home")
    public ModelAndView home(@RequestParam("page") Optional<Integer> page, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        userSession(session, request, response);
        ModelAndView mv = new ModelAndView("home/home");
        //search login and add session
        Login login = (Login) session.getAttribute(USER_AUTHENTICATED);
        //set message initial
        setMessageUser(mv, login);
        //get list contacts
        int currentPage = page.orElse(1) - 1;

        PageRequest pageable = PageRequest.of(currentPage, 8, Sort.by("name"));
        Page<Contact> listPageable = contactService.findAllContactByIdLogin(login.getId(), pageable);

        mv.addObject("list", listPageable);

        int totalPages = listPageable.getTotalPages();
        if (totalPages > 0) {
            List<Integer> numberPage = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
            mv.addObject("numberPages", numberPage);
        }
        //sectionHome
        mv.addObject("sectionHome", Boolean.TRUE);
        return mv;
    }

    @GetMapping("/criar")
    public ModelAndView criar() {
        return new ModelAndView("criar/criar");
    }

    @PostMapping("/create-account")
    public ModelAndView createAccount(@Valid LoginRequest login, BindingResult bindingResult, HttpServletResponse response) throws UnsupportedEncodingException {
        if (bindingResult.hasErrors()) {
            //error camps
            return new ModelAndView("criar/criar");
        } else {
            //create valid, verify username
            if (loginService.findLoginByUsername(login.getUsername()) == null) {
                //username valid, create account
                login.setPassword(getPasswordEncoder().encode(login.getPassword()));
                Login loginCreate = loginService.saveLogin(login);
                //create session user
                CookieService.setCookie(response, USER_AUTHENTICATED, String.valueOf(loginCreate.getId()), 60 * 60 * 24);
                return new ModelAndView("redirect:/home");
            } else {
                //username not valid return error
                ModelAndView mv = new ModelAndView("criar/criar");
                return mv.addObject("errorUser", Boolean.TRUE);
            }

        }
    }

    @GetMapping("/esqueceu")
    public ModelAndView esqueceu() {
        return new ModelAndView("esqueceu/esqueceu");
    }

    @PostMapping("/remember-password")
    public ModelAndView rememberPassword(@Valid RememberPassword rememberPassword, BindingResult bindingResult, HttpServletResponse response) throws UnsupportedEncodingException {
        if (bindingResult.hasErrors()) {
            //error camp invalid!
            return new ModelAndView("esqueceu/esqueceu");
        } else {
            //check valid username
            Login loginByUsername = loginService.findLoginByUsername(rememberPassword.getUsername());
            if (loginByUsername != null) {
                //exist username, set new password
                loginByUsername.setPassword(getPasswordEncoder().encode(rememberPassword.getPassword()));
                loginService.replaceLogin(loginByUsername);
                Login loginNew = loginService.findLoginByUsername(rememberPassword.getUsername());
                LoginSingInRequest loginSingInRequest = new LoginSingInRequest();
                loginSingInRequest.setPassword(loginNew.getPassword());
                loginSingInRequest.setUsername(loginNew.getUsername());
                return singInAfterUpdatePassword(loginSingInRequest, response);
            } else {
                //username not found
                ModelAndView mv = new ModelAndView("esqueceu/esqueceu");
                mv.addObject("userInvalid", Boolean.TRUE);
                return mv;
            }
        }
    }

    @GetMapping("/editarPerfil")
    public ModelAndView editarPerfil(HttpSession session, HttpServletRequest request, HttpServletResponse response, LoginRequest loginRequest) throws UnsupportedEncodingException {
        userSession(session, request, response);
        ModelAndView mv = new ModelAndView("home/editarPerfil");
        //set message user
        Login login = (Login) session.getAttribute(USER_AUTHENTICATED);
        setMessageUser(mv, login);
        //section sectionEditarPerfil
        mv.addObject("sectionEditarPerfil", Boolean.TRUE);
        //set object loginRequest
        loginRequest.fromLogin(login);
        return mv;
    }

    @PostMapping("/updatePerfil")
    public ModelAndView updatePerfil(@Valid LoginRequest loginRequest, BindingResult bindingResult, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        userSession(session, request, response);
        Login login = (Login) session.getAttribute(USER_AUTHENTICATED);
        if (bindingResult.hasErrors()) {
            //error camp inválid!
            ModelAndView mv = new ModelAndView("home/editarPerfil");
            //set message user
            setMessageUser(mv, login);
            //section sectionEditarPerfil
            mv.addObject("sectionEditarPerfil", Boolean.TRUE);
            return mv;
        } else {
            //sucess check username
            Login loginByUsername = loginService.findLoginByUsername(loginRequest.getUsername());
            //user loged
            Login userLogin = (Login) session.getAttribute(USER_AUTHENTICATED);
            if (userLogin.getUsername().equals(loginRequest.getUsername()) || loginByUsername == null) {
                //edit login success full
                loginRequest.setPassword(getPasswordEncoder().encode(loginRequest.getPassword()));
                loginService.replaceLogin(loginRequest, userLogin.getId());
                ModelAndView mv = new ModelAndView("redirect:/home");
                mv.addObject("success", "Perfil editado com sucesso!");
                return mv;
            } else {
                //error edit login
                ModelAndView mv = new ModelAndView("home/editarPerfil");
                //set message user
                setMessageUser(mv, login);
                //section sectionEditarPerfil
                mv.addObject("sectionEditarPerfil", Boolean.TRUE);
                mv.addObject("errorUser", Boolean.TRUE);
                return mv;
            }

        }
    }

    @GetMapping("/novoContato")
    public ModelAndView novoContato(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        userSession(session, request, response);
        Login login = (Login) session.getAttribute(USER_AUTHENTICATED);
        ModelAndView mv = new ModelAndView("home/novoContato");
        mv.addObject("sectionNovoContato", Boolean.TRUE);
        setMessageUser(mv, login);
        return mv;
    }

    @PostMapping("/adicionarContato")
    public ModelAndView adicionarContato(@Valid ContactRequest contactRequest, BindingResult bindingResult, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        userSession(session, request, response);
        Login login = (Login) session.getAttribute(USER_AUTHENTICATED);
        if (bindingResult.hasErrors()) {
            //error camp invalid
            ModelAndView mv = new ModelAndView("home/novoContato");

            return mv;
        } else {
            //success and add contact
            if (contactRequest.getEmail().equals("")) {
                contactRequest.setEmail("Não possui e-mail!");
            }
            contactRequest.setLogin(login);
            contactService.saveContact(contactRequest);
            ModelAndView mv = new ModelAndView("redirect:/home");
            setMessageUser(mv, login);
            mv.addObject("success", "Contato Salvo com sucesso!");
            return mv;
        }
    }

    @GetMapping("/delete/{id}")
    public ModelAndView deletarContato(@PathVariable String id) {
        try {
            contactService.deleteContactById(UUID.fromString(id));
            ModelAndView mv = new ModelAndView("redirect:/home");
            mv.addObject("success", "Contato excluído com sucesso!");
            return mv;
        } catch (Exception e) {
            ModelAndView mv = new ModelAndView("redirect:/home");
            mv.addObject("error", "Erro ao excluir contato!");
            return mv;

        }
    }

    @GetMapping("/{id}")
    public ModelAndView editarContato(@PathVariable String id, ContactRequest contactRequest, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        try {
            userSession(session, request, response);
            Contact contact = contactService.findContactById(UUID.fromString(id));
            Login login = (Login) session.getAttribute(USER_AUTHENTICATED);
            if (contact != null) {
                //contact exist
                contactRequest.fromContact(contact);
                ModelAndView mv = new ModelAndView("home/editarContato");
                setMessageUser(mv, login);
                mv.addObject("contactId", contact.getId());
                return mv;
            } else {
                //contact not exist
                ModelAndView mv = new ModelAndView("redirect:/home");
                mv.addObject("error", "Erro ao procurar contato!");
                return mv;
            }
        } catch (Exception e) {
            //contact not exist
            ModelAndView mv = new ModelAndView("redirect:/home");
            mv.addObject("error", "Erro ao procurar contato!");
            return mv;
        }

    }

    @PostMapping("/{id}/editar")
    public ModelAndView confirmarEdicaoContato(@Valid ContactRequest contactRequest, BindingResult bindingResult, @PathVariable UUID id, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        userSession(session, request, response);
        Login login = (Login) session.getAttribute(USER_AUTHENTICATED);
        if (bindingResult.hasErrors()) {
            //error camp invalid
            ModelAndView mv = new ModelAndView("home/editarContato");
            mv.addObject("contactId", id);
            setMessageUser(mv, login);
            return mv;
        } else {
            //edit contact
            if (contactRequest.getEmail().equals("")) {
                contactRequest.setEmail("Não possui e-mail!");
            }
            contactRequest.setLogin(login);
            contactService.replaceContact(contactRequest, id);
            ModelAndView mv = new ModelAndView("redirect:/home");
            setMessageUser(mv, login);
            mv.addObject("success", "Contato Editado com sucesso!");
            return mv;
        }
    }

    @GetMapping("/limparLista")
    public ModelAndView limparLista(HttpSession session, HttpServletRequest request, HttpServletResponse response, Pageable pageable) throws UnsupportedEncodingException {
        userSession(session, request, response);
        Login login = (Login) session.getAttribute(USER_AUTHENTICATED);
        if (contactService.findAllContactByIdLogin(login.getId(), pageable).getTotalElements() > 0) {
            contactService.deleteAllContactByIdLogin(login.getId());
            ModelAndView mv = new ModelAndView("redirect:/home");
            mv.addObject("success", "Lista limpada com sucesso!");
            return mv;
        } else {
            ModelAndView mv = new ModelAndView("redirect:/home");
            mv.addObject("error", "Erro ao limpar Lista,pois ela está vazia!");
            return mv;
        }

    }

    @GetMapping("/relatorio")
    public ModelAndView relatorio(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException, DocumentException {
        userSession(session, request, response);
        Document document = new Document();
        Login login = (Login) session.getAttribute(USER_AUTHENTICATED);
        List<Contact> contacts = contactService.findAllContactByIdLogin(login.getId());
        if (contacts.size() > 0) {
            try {
                response.setContentType("Apllication/pdf");
                response.addHeader("Content-Disposition", "inline; filename=" + "contatos.pdf");
                PdfWriter.getInstance(document, response.getOutputStream());
                document.open();
                document.add(new Paragraph("Relatório dos contatos de " + login.getName() + " :"));
                document.add(new Paragraph(" "));
                PdfPTable table = new PdfPTable(3);
                table.addCell(new PdfPCell(new Paragraph("Nome")));
                table.addCell(new PdfPCell(new Paragraph("Telefone")));
                table.addCell(new PdfPCell(new Paragraph("E-mail")));
                for (Contact contactsList : contacts) {
                    table.addCell(contactsList.getName());
                    table.addCell(contactsList.getNumber().toString());
                    table.addCell(contactsList.getEmail());
                }
                document.add(table);
                document.close();
                ModelAndView mv = new ModelAndView("redirect:/home");
                mv.addObject("success", "Relatório gerado com sucesso!");
                return mv;
            } catch (DocumentException | IOException e) {
                document.close();
                ModelAndView mv = new ModelAndView("redirect:/home");
                mv.addObject("error", "Erro ao gerar relatório!");
                return mv;
            }
        } else {
            ModelAndView mv = new ModelAndView("redirect:/home");
            mv.addObject("error", "Erro ao gerar relatório, pois você não possui contatos!");
            return mv;
        }

    }


    @GetMapping("/logout")
    public ModelAndView logout(HttpSession session, HttpServletResponse response) throws UnsupportedEncodingException {
        ModelAndView mv = new ModelAndView("redirect:/");
        mv.addObject("logout", Boolean.TRUE);
        CookieService.setCookie(response, USER_AUTHENTICATED, "", 0);
        session.invalidate();
        return mv;
    }

    @ModelAttribute(value = "loginRequest")
    public LoginRequest getLoginRequest() {
        return new LoginRequest();
    }

    @ModelAttribute(value = "rememberPassword")
    public RememberPassword getRememberPassword() {
        return new RememberPassword();
    }

    @ModelAttribute(value = "loginSingInRequest")
    public LoginSingInRequest getLoginSingInRequest() {
        return new LoginSingInRequest();
    }

    @ModelAttribute(value = "contactRequest")
    public ContactRequest getContactRequest() {
        return new ContactRequest();
    }
}
