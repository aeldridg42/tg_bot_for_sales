  const BASE_URL = 'https://lamia.serveo.net';
  const PRODUCTS_API_BASE_URL = 'products.json';
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
        const img = document.createElement('img');
        img.src = BASE_URL + "/images/" + product.previewImageId;
        img.classList.add('img');
        img.setAttribute('id', 'img');
        img.style.height = '140px';
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
                item = 'product=' + `${product.id}`;
                tg.MainButton.show();
              }
              else {
                tg.MainButton.hide();
                tg.MainButton.setText('Continue')
              }
            }
            );
        const showMoreBtn = document.createElement('button');
        showMoreBtn.classList.add('show_more_btn')
        showMoreBtn.setAttribute('id', `show_btn${product.id}`);
        showMoreBtn.innerHTML = 'подробнее';
        aboutDiv.append(showMoreBtn);

        // --> Creating DOM elements for "Show more" button <-- //

        showMoreBtn.addEventListener('click', function(){
          const wrap = document.getElementById('wrapper');
          if(wrap.style.display !== 'none'){
            wrap.style.display = 'none';
          }
          const showMoreDiv = document.createElement('div');
          showMoreDiv.classList.add("show_more_div");
          showMoreDiv.setAttribute('id', 'show_more');
          document.body.appendChild(showMoreDiv);
          const showMoreh3 = document.createElement('h3');
          showMoreh3.setAttribute('id', 'show_more_about');
          showMoreh3.textContent = product.name;
          const showMoreP = document.createElement('p');
          showMoreP.setAttribute('id', 'show_more_about');
          showMoreP.textContent = product.description;

          // --> Creating Slideshow container  <--//
          const slideShowContainer = document.createElement('div');
          slideShowContainer.classList.add('slideshow-container');
          product.images.forEach(image => {
            console.log(image);
            const slideImgDiv = document.createElement('div');
            slideImgDiv.classList.add('slideImg');
            const slideImage = document.createElement('img');
            slideShowContainer.append(slideImgDiv);
            slideImage.src = BASE_URL + '/images/'+ `${image}`;
            slideImage.style.width = '300px'
            slideImage.style.height = '350px'
            slideImgDiv.append(slideImage);
          })
          //  let slides = document.getElementsByClassName("slideImg");
          //    console.log(slides);
          const prevA = document.createElement('a');
          prevA.classList.add('prev');
          prevA.innerHTML = '❮'
          //prevA.addEventListener('click', plusSlides(-1));
          const nextA = document.createElement('a');
          nextA.classList.add('next');
          nextA.innerHTML = '❯'
          //nextA.addEventListener('click', plusSlides(1));
          slideShowContainer.append(nextA);
          slideShowContainer.append(prevA);
          const backBtn = document.createElement('button');
          backBtn.classList.add('back_btn');
          backBtn.innerHTML = '<--'
          showMoreDiv.append(backBtn);
          showMoreDiv.append(showMoreh3);
          showMoreDiv.append(slideShowContainer);
          showMoreDiv.append(showMoreP);
          const btnAddProd = document.createElement('button');
          btnAddProd.classList.add('btnAddProd');
          btnAddProd.setAttribute('id', `btnAddProd${product.id}`);
          btnAddProd.innerHTML = 'Добавить';
          showMoreDiv.append(btnAddProd);
          backBtn.addEventListener('click', function(){
            if(wrap.style.display === 'none'){
              showMoreDiv.remove();
              wrap.style.display = 'block';
            }
          })
        })
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

