
const modal = document.getElementById("modal");
const openModal = document.getElementById("openModal");
const closeModal = document.querySelector(".close");

document.getElementById("cancelarBtn").addEventListener("click", function () {
    document.getElementById("modal").style.display = "none";
});

openModal.addEventListener("click", function (event) {
    event.preventDefault();
    modal.style.display = "flex";
});

closeModal.addEventListener("click", function () {
    modal.style.display = "none";
});

window.addEventListener("click", function (event) {
    if (event.target === modal) {
        modal.style.display = "none";
    }
});
