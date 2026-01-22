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

    // viết cho nut bấm tải thêm
    // Cấu hình: Muốn hiện bao nhiêu cái ban đầu
    const itemsPerPage = 6; 
    
    // Lấy danh sách tất cả sản phẩm
    // Lưu ý: '.product-grid .product-card' nghĩa là tìm thẻ card nằm trong grid
    let cards = document.querySelectorAll('.product-grid .product-card');

    // --- HÀM 1: KHỞI TẠO (Chạy ngay khi vào trang) ---
    function init() {
        // Vòng for duyệt qua tất cả sản phẩm
        for (let i = 0; i < cards.length; i++) {
            // Logic: Nếu số thứ tự (i) nhỏ hơn 6 -> Hiện, ngược lại -> Ẩn
            if (i < itemsPerPage) {
                cards[i].style.display = ''; // Để rỗng là nó tự ăn theo CSS của Grid
            } else {
                cards[i].style.display = 'none'; // Ẩn đi
            }
        }
        
        // Nếu tổng sản phẩm nhỏ hơn 6 thì ẩn luôn nút Load More
        if (cards.length <= itemsPerPage) {
            document.getElementById('loadMoreBtn').style.display = 'none';
        }
    }

    // --- HÀM 2: XỬ LÝ KHI BẤM NÚT TẢI THÊM ---
    function loadMore() {
        let count = 0; // Đếm xem đã hiện thêm được bao nhiêu cái rồi
        
        // Lại duyệt qua danh sách để tìm những cái đang bị ẩn
        for (let i = 0; i < cards.length; i++) {
            // Nếu tìm thấy cái nào đang ẩn (display == 'none')
            if (cards[i].style.display === 'none') {
                cards[i].style.display = ''; // Hiện nó ra
                count++; // Tăng biến đếm
                
                // Nếu đã hiện đủ 6 cái của đợt này rồi thì dừng lại, không mở thêm nữa
                if (count >= itemsPerPage) {
                    break; 
                }
            }
        }

        // Kiểm tra xem còn cái nào ẩn không? Nếu hiện hết rồi thì ẩn nút đi
        let hiddenItems = Array.from(cards).filter(c => c.style.display === 'none');
        if (hiddenItems.length === 0) {
            document.getElementById('loadMoreBtn').style.display = 'none';
        }
    }

    // Kích hoạt hàm khởi tạo ngay
    init();
