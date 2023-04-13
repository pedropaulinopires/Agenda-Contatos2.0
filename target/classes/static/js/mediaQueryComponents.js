let menuDesktop = document.getElementById("menu");
    if(window.matchMedia("(min-width:800px)")){
        menuDesktop.classList.add("active")
    } else if("(max-width:799px)") {
        menuDesktop.classList.remove("active")
    }


 
