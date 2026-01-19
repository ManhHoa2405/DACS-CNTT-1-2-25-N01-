document.addEventListener("DOMContentLoaded", function() {
    // Xử lý Accordion Sidebar
    const acc = document.getElementsByClassName("accordion");
    
    for (let i = 0; i < acc.length; i++) {
        acc[i].addEventListener("click", function() {
            /* Toggle class active cho nút bấm */
            this.classList.toggle("active");
            
            /* Tìm panel tương ứng để ẩn/hiện */
            const panel = this.nextElementSibling;
            if (panel.style.display === "block") {
                panel.style.display = "none";
                this.querySelector("i").className = "fa-solid fa-plus";
            } else {
                panel.style.display = "block";
                this.querySelector("i").className = "fa-solid fa-minus";
            }
        });
    }
});



    // 1. Hàm chạy khi bấm nút Size
    function filterSize(size) {
        // Kiểm tra: Nếu bấm lại size đang chọn thì bỏ chọn (Hủy lọc size)
        const currentSize = document.getElementById('hiddenSize').value;
        if (currentSize === size) {
            document.getElementById('hiddenSize').value = ''; // Bỏ chọn
        } else {
            document.getElementById('hiddenSize').value = size; // Chọn size mới
        }
        applyFilter(); // Gọi hàm lọc chung
    }

    // 2. Hàm lọc chung (Xử lý URL và Reload trang)
    function applyFilter() {
        // Lấy các tham số hiện tại trên URL (Keyword, Category...)
        const params = new URLSearchParams(window.location.search);

        // --- LẤY GIÁ TRỊ TỪ HTML ---
        
        // A. Lấy Sort (Sắp xếp)
        const sortRadios = document.getElementsByName('sort');
        for (let radio of sortRadios) {
            if (radio.checked) {
                params.set('sort', radio.value);
                break;
            }
        }

        // B. Lấy Size
        const sizeVal = document.getElementById('hiddenSize').value;
        if (sizeVal) {
            params.set('size', sizeVal);
        } else {
            params.delete('size'); // Nếu không chọn size thì xóa tham số này khỏi URL
        }

        // --- CHUYỂN TRANG ---
        // Ví dụ: /user/showProduct?categoryName=Áo&size=L&sort=price_asc
        window.location.href = window.location.pathname + "?" + params.toString();
    }
