const BASE_URL = 'https://059d-176-52-22-229.eu.ngrok.io';
const PRODUCTS_API_BASE_URL = 'temp.json';
let tg = window.Telegram.WebApp;

  tg.expand();

  tg.MainButton.textColor = '#FFFFFF';
  tg.MainButton.color = '#2cab37'


let item = '';

  

  fenchAndAppendProducts();
  
  async function fenchAndAppendProducts(){
    const products =  await fetchProducts();
      const productsByCategory = getproductsByCategory(products);
      const wrapper = document.getElementById('wrapper');
      for(const [category, products] of Object.entries(productsByCategory)){
      const categoryDiv = createCategory(category, products);
      wrapper.append(categoryDiv);
    }
  }

  

   function createCategory(category, products){
    const categoryDiv = document.createElement('div');
    categoryDiv.classList.add('category');
    const h2 = document.createElement('h2');
    h2.textContent = category;
    categoryDiv.append(h2);
    products.forEach(product => {
      const productDiv = document.createElement('div');
      productDiv.classList.add('product');
      var img = document.createElement('img');
      var xhr = new XMLHttpRequest();
    xhr.responseType = 'blob'; //so you can access the response like a normal URL
    xhr.onreadystatechange = function () {
    if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
      img.src = URL.createObjectURL(xhr.response); //create <img> with src set to the blob
        }
      };
      xhr.open('GET', BASE_URL + "/images/" + product.previewImageId, true);
      xhr.setRequestHeader('ngrok-skip-browser-warning', 'password123');
      xhr.send();
      img.classList.add('img');
      img.setAttribute('id', 'img');
      img.style.height = product.height;
      img.style.width = product.width;
      productDiv.append(img);
      const aboutDiv = document.createElement('div')
      aboutDiv.classList.add('about');
      productDiv.append(aboutDiv);
      const h3 = document.createElement('h3');
      h3.setAttribute('id', 'about');
      h3.textContent = product.name;
      const p = document.createElement('p');
      p.setAttribute('id', 'about');
      p.textContent = product.description;
      aboutDiv.append(h3);
      aboutDiv.append(p);
      const priceAndBtnDiv = document.createElement('div')
      priceAndBtnDiv.classList.add('priceAndBtn');
      productDiv.append(priceAndBtnDiv);
      const price = document.createElement('h4');
      price.setAttribute('id', 'right_box');
      price.textContent = `${product.price} P`;
      const btn = document.createElement('button');
      btn.classList.add('btn');
      btn.setAttribute('id', `btn${product.id}`);
      btn.innerHTML = '+';
      priceAndBtnDiv.append(price);
      priceAndBtnDiv.append(btn);
      categoryDiv.append(productDiv);
      btn.addEventListener('click', function(){
          let isk = `Вы выбрали товар ${product.id}`
          if (tg.MainButton.text != isk){
              tg.MainButton.setText(isk)
              item = `${product.id}`;
              tg.MainButton.show();
            }
            else {
              tg.MainButton.hide();
              tg.MainButton.setText('Continue')
            }
          }
          );
    });
    console.log(categoryDiv);
    return categoryDiv;
  }
  
      async function fetchProducts(){
      const response =  await fetch(PRODUCTS_API_BASE_URL);
      const products =  await response.json();
      console.log(products);
      return products;
  }

  function getproductsByCategory(products){
  const productsByCategory = {};
    products.forEach(product => {
      if(productsByCategory.hasOwnProperty(product.category)){
          productsByCategory[product.category].push(product);
      } else{
          productsByCategory[product.category] = [product];
      }
    });
    console.log(productsByCategory);
    return productsByCategory;
  }
  
  Telegram.WebApp.onEvent('mainButtonClicked', function(){
    tg.sendData(item);
  });

  let usercard = document.getElementById('usercard');
  let p = document.createElement('p');

  p.innerText = `${tg.initDataUnsafe.user.first_name}
  ${tg.initDataUnsafe.user.last_name}`;

  usercard.appendChild(p);

