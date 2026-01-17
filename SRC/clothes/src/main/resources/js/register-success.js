function showSuccessModal() {
  const modal = document.getElementById("successModal");
  modal.style.display = "flex";

  setTimeout(() => {
    window.location.href = "/templates/user/homePage.html";
  }, 3000);
}

function closeSuccess() {
  window.location.href = "/templates/user/homePage.html";
}
