     
  const PRODUCTS_API_BASE_URL = 'https://localhost:5000/api/products';
  // let tg = window.Telegram.WebApp;
  //
  // tg.expand();
  //
  // tg.MainButton.textColor = '#FFFFFF';
  // tg.MainButton.color = '#2cab37'

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

//   async function fenchAndAppendProducts(){
//     const products = await fetchProducts();
//     const productsByCategory = getproductsByCategory(products);

//     const wrapper = document.getElementById('wrapper');
//     for(const [category, products] of Object.entries(productsByCategory)){
//     const categoryDiv = createCategory(category, products);
//     wrapper.append(categoryDiv);
//   }
// }

  // let button = document.getElementById(`btn${products.id}`)

  // button.addEventListener('click', function(){
  //   if(tg.MainButton.isVisible){
  //     tg.MainButton.hide();
  //   }
  //   else{
  //     tg.MainButton.seText(`Вы выбрали товар${products.id}`)
  //     item = `${products.id}`;
  //     tg.MainButton.show();
  //   }
  // })

  // Telegram.WebbApp.onEvent('mainButtonClicked', function(){
  //   tg.sendData(item);
  // });

  // let usercard = document.getElementById('usercard');
  // let p = document.createElement('p');
  // p.innerText = `${tg.initDataUnsafe.user.first_name}
  // ${tg.initDataUnsafe.user.last_name}`

  // usercard.appendChild(p);


  function createCategory(category, products){
    const categoryDiv = document.createElement('div');
    categoryDiv.classList.add('category');
    const h2 = document.createElement('h2');
    h2.textContent = category;
    categoryDiv.append(h2);
    products.forEach(product => {
      const productDiv = document.createElement('div');
      productDiv.classList.add('product');
      const img = document.createElement('img');
      img.classList.add('img');
      img.setAttribute('id', 'img');
      img.src = product.picture_url;
      img.style.width = '150px';
      img.style.height = '190px';
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
      btn.innerHTML = 'Добавить';
      priceAndBtnDiv.append(price);
      priceAndBtnDiv.append(btn);
      categoryDiv.append(productDiv);
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
  
 


