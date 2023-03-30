    const PRODUCTS_API_BASE_URL = 'https://0bee-176-52-25-14.eu.ngrok.io/api/products';
    
    fenchAndAppendProducts();
    

    async function fenchAndAppendProducts(){
        const products = await fetchProducts();
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
        img.classList.add('img');
        img.setAttribute('id', 'img');
        img.src = product.picture_url;
        img.style.width = '150px';
        img.style.height = '190px';
        productDiv.append(img);
        const h3 = document.createElement('h3');
        h3.setAttribute('id', 'about');
        h3.textContent = product.name;
        const p = document.createElement('p');
        p.setAttribute('id', 'about');
        p.textContent = product.description;
        const price = document.createElement('h4');
        price.setAttribute('id', 'right_box');
        price.textContent = product.price;
        const btn = document.createElement('button');
        btn.classList.add('btn');
        btn.setAttribute('id', 'right_box');
        btn.innerHTML = 'Добавить';
        productDiv.append(h3);
        productDiv.append(p);
        productDiv.append(price);
        productDiv.append(btn);
        categoryDiv.append(productDiv);
      });
      console.log(categoryDiv);
      return categoryDiv;
    }
    
    async function fetchProducts(){
        const response = await fetch(PRODUCTS_API_BASE_URL);
        const products = await response.json();
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
 

  