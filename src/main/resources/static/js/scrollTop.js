const toTop = document.querySelector(".scrollToTop");

function scrollToTop(){
    window.scrollTo(0,0);
}

window.addEventListener("scroll", () =>{
    if(window.pageYOffset > 500){
        toTop.classList.add("active")
    } else {
        toTop.classList.remove("active")
    }
});


