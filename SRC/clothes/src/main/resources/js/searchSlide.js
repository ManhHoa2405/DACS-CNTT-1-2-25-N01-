function toggleSearch() {
  const search = document.getElementById("searchSlide");
  const overlay = document.getElementById("searchOverlay");
  const content = document.getElementById("pageContent");

  const isActive = search.classList.contains("active");

  search.classList.toggle("active");
  overlay.classList.toggle("active");
  content.classList.toggle("blur");

  if (!isActive) {
    setTimeout(() => {
      search.querySelector("input").focus();
    }, 200);
  }
}
