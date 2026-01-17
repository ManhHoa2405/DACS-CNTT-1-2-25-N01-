document.addEventListener("DOMContentLoaded", () => {
  const currentPage = window.location.pathname.split("/").pop();

  document.querySelectorAll(".side-bar li").forEach((li) => {
    if (li.dataset.page === currentPage) {
      li.classList.add("active");
    }
  });
});
