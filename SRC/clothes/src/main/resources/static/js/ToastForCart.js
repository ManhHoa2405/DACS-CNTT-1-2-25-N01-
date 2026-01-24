function showToast(message) {
  const x = document.getElementById("toast");
  x.innerText = message;
  x.className = "show";
  // Ẩn sau 3 giây
  setTimeout(function () {
    x.className = x.className.replace("show", "");
  }, 3000);
}
