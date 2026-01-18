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

function checkPasswordMatch() {
        var password = document.getElementById("password").value;
        var confirmPassword = document.getElementById("confirmPassword").value;

        if (password !== confirmPassword) {
            alert(" Mật khẩu nhập lại không khớp! Vui lòng kiểm tra lại.");
            return false; // Chặn không cho form gửi đi
        }
        return true; // Cho phép gửi đi
    }