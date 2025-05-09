const products = Array.from({ length: 20 }, (_, i) => ({
  id: i + 1,
  name: `Product ${i + 1}`,
  price: (Math.random() * 100 + 1).toFixed(2),
  image: `https://picsum.photos/seed/${i + 1}/500/400`
}));

function loadProducts() {
  const container = document.getElementById('products-container');
  if (!container) return;
  container.innerHTML = '';
  products.forEach(p => {
    const div = document.createElement('div');
    div.className = 'product';
    div.innerHTML = `
      <img src="${p.image}" alt="${p.name}">
      <h3>${p.name}</h3>
      <p>$${p.price}</p>
      <button class="btn" onclick="addToCart(${p.id})">Add to Cart</button>
    `;
    container.appendChild(div);
  });
}

function loadList() {
  const container = document.getElementById('list-container');
  if (!container) return;
  container.innerHTML = '';
  products.forEach(p => {
    const div = document.createElement('div');
    div.className = 'list-item';
    div.innerHTML = `
      <img src="${p.image}" alt="${p.name}">
      <div>
        <h3>${p.name}</h3>
        <p>$${p.price}</p>
        <button class="btn" onclick="addToCart(${p.id})">Add to Cart</button>
      </div>
    `;
    container.appendChild(div);
  });
}

function loadCart() {
  const container = document.getElementById('cart-items');
  if (!container) return;
  const cart = JSON.parse(localStorage.getItem('cart')) || [];
  container.innerHTML = '';
  if (cart.length === 0) {
    container.innerHTML = '<p>Your cart is empty.</p>';
    return;
  }
  cart.forEach((item, index) => {
    const div = document.createElement('div');
    div.className = 'product';
    div.innerHTML = `
      <img src="${item.image}" alt="${item.name}">
      <div>
        <h3>${item.name}</h3>
        <p>$${item.price}</p>
        <button class="btn btn-remove" onclick="removeFromCart(${index})">Remove</button>
      </div>
    `;
    container.appendChild(div);
  });
}

function updateCartCount() {
  const cart = JSON.parse(localStorage.getItem('cart')) || [];
  document.querySelectorAll('#cart-count').forEach(el => el.textContent = cart.length);
}

function addToCart(id) {
  const cart = JSON.parse(localStorage.getItem('cart')) || [];
  const product = products.find(p => p.id === id);
  cart.push(product);
  localStorage.setItem('cart', JSON.stringify(cart));
  updateCartCount();
}

function removeFromCart(index) {
  const cart = JSON.parse(localStorage.getItem('cart')) || [];
  cart.splice(index, 1);
  localStorage.setItem('cart', JSON.stringify(cart));
  updateCartCount();
  loadCart();
}

function clearCart() {
  localStorage.removeItem('cart');
  updateCartCount();
  loadCart();
}

document.addEventListener('DOMContentLoaded', () => {
  loadProducts();
  loadList();
  updateCartCount();
  if (window.location.pathname.endsWith('cart.html')) {
    loadCart();
    document.getElementById('clear-cart').addEventListener('click', clearCart);
  }
});
