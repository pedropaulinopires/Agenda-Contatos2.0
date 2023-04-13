package com.pedro.spring.service;

import com.pedro.spring.domain.Contact;
import com.pedro.spring.domain.Login;
import com.pedro.spring.repository.ContactRepository;
import com.pedro.spring.request.ContactRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;

    //save Contact
    public Contact saveContact(ContactRequest contact) {
        return contactRepository.save(contact.build());
    }

    //findById
    public Contact findContactById(UUID id) {
        return contactRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Contact not found by id")
        );
    }


    //replace
    public void replaceContact(ContactRequest contact, UUID id) {
        findContactById(id);
        contactRepository.save(contact.build(contact, id));
    }

    //delete
    public void deleteContactById(UUID id) {
        contactRepository.delete(findContactById(id));
    }
    //delete
    public void deleteAllContactByIdLogin(UUID id) {
        contactRepository.deleteAllContactByLogin(id);
    }


    //find all contacts by id login
    public Page<Contact> findAllContactByIdLogin(UUID id, Pageable pageable){
        return contactRepository.findAllByIdLogin(id,pageable);
    }

    public List<Contact> findAllContactByIdLogin(UUID id){
        return contactRepository.findAllByIdLogin(id);
    }

}
