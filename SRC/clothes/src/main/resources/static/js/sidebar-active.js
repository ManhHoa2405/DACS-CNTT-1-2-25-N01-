document.addEventListener("DOMContentLoaded", () => {
  const currentPage = window.location.pathname.split("/").pop();

  document.querySelectorAll(".side-bar li").forEach((li) => {
    if (li.dataset.page === currentPage) {
      li.classList.add("active");
    }
  });
});



// 1. Hàm xem trước ảnh (Preview Image)
        function previewImage(input) {
            if (input.files && input.files[0]) {
                var reader = new FileReader();
                reader.onload = function(e) {
                    // Tìm thẻ img nằm cùng cấp với label cha của input
                    // Cấu trúc: div > [img, label > input]
                    var imgElement = input.parentElement.previousElementSibling;
                    imgElement.src = e.target.result;
                }
                reader.readAsDataURL(input.files[0]);
            }
        }

        // 2. Hàm thêm dòng Size/Số lượng mới
        function addVariant() {
            var container = document.getElementById("variant-container");
            
            // Tạo một div mới
            var newRow = document.createElement("div");
            newRow.classList.add("variant-row");
            newRow.style.marginTop = "10px"; // Thêm chút khoảng cách
            
            // Nội dung HTML của dòng mới
            newRow.innerHTML = `
                <input type="text" name="sizes" placeholder="Size mới" required />
                <input type="number" name="quantities" value="0" min="0" required />
                <button type="button" class="btn-delete" onclick="removeVariant(this)">Xóa</button>
            `;
            
            container.appendChild(newRow);
        }

        // 3. Hàm xóa dòng Size
        function removeVariant(button) {
            var row = button.parentElement;
            // Kiểm tra nếu còn nhiều hơn 1 dòng thì mới cho xóa (để lại ít nhất 1 dòng)
            var container = document.getElementById("variant-container");
            if (container.children.length > 1) {
                row.remove();
            } else {
                alert("Phải có ít nhất một size!");
            }
        }


// 4. Hàm xử lý hiển thị thông báo (alert) khi có message từ server
//    Đã chuyển đoạn này vào thẻ <script> trong file addProduct.html        
// function showAlert(message) {
//     if (message) {
//         alert(message);
//     }
// }