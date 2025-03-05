package DJ.MyDigital.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import DJ.MyDigital.Model.Farmer;
import DJ.MyDigital.Model.Product;
import DJ.MyDigital.service.FarmerService;
import DJ.MyDigital.service.ProductService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/Farmer/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private FarmerService farmerService;

    @PostMapping("/save")
    public String saveProduct(@ModelAttribute("product") Product product, HttpSession session) {
        Long farmerId = (Long) session.getAttribute("farmerId");  // Ensure this attribute is being set properly
    
        if (farmerId != null) {
            Optional<Farmer> optionalFarmer = farmerService.getFarmerById(farmerId);
            if (optionalFarmer.isPresent()) {
                product.setFarmer(optionalFarmer.get());
                productService.saveProduct(product);
                return "redirect:/Farmer/FarmerHome";
            }
        }
        return "error-page"; // Ensure this Thymeleaf template exists
    }
    

    @GetMapping("/farmer/{farmerId}")
public String getProductsByFarmer(@PathVariable("farmerId") Long farmerId, Model model) {
        Optional<Farmer> optionalFarmer = farmerService.getFarmerById(farmerId);
        if (optionalFarmer.isPresent()) {
            List<Product> products = productService.getProductsByFarmerId(farmerId);
            model.addAttribute("products", products);
            model.addAttribute("farmer", optionalFarmer.get());
            return "adminproduct-edit"; 
        }
        return "error-page";
    }

    @GetMapping("/new")
public String showForm(Model model) {
    model.addAttribute("product", new Product());
    return "product-form";  
}

@GetMapping("/view")
public String getAllProducts(Model model, HttpSession session) {
    Long farmerId = (Long) session.getAttribute("farmerId"); 
    
    if (farmerId != null) {
        List<Product> products = productService.getProductsByFarmerId(farmerId);
        model.addAttribute("products", products);
        return "farmer-product-list";  
    }

    return "error-page";
}

@GetMapping("/edit/{id}")
public String showEditForm(@PathVariable Long id, Model model) {
    Product product = productService.getProductById(id);
    if (product != null) {
        model.addAttribute("product", product);
        return "edit-product";
    }
    return "redirect:/Farmer/FarmerHome";
}

@PostMapping("/update/{id}")
public String updateProduct(@PathVariable Long id, @ModelAttribute Product updatedProduct, HttpSession session) {
    Product existingProduct = productService.getProductById(id);

    if (existingProduct != null) {
        updatedProduct.setId(id); // Ensure ID is set
        updatedProduct.setFarmer(existingProduct.getFarmer()); // Keep farmer reference
        productService.saveProduct(updatedProduct);
    }

    return "redirect:/Farmer/FarmerHome";
}



@PostMapping("/delete/{id}")
public String deleteProduct(@PathVariable Long id) {
    productService.deleteProduct(id);
    return "redirect:/Farmer/FarmerHome";
}

@GetMapping()
    public String listProducts(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "Adminproduct-list";
    }

    @PostMapping("/admin/update")
    public String updateAdminProduct(@ModelAttribute Product product) {
        Product existingProduct = productService.getProductById(product.getId());
        if (existingProduct != null) {
            product.setFarmer(existingProduct.getFarmer()); // Keep the merchant reference
            productService.updateProduct(product);
        }
        return "redirect:/Farmer/products";
    }

    @GetMapping("/admin/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "adminproduct-edit";
    }

    @GetMapping("/admin/delete/{id}")
    public String deleteProductAdmin(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/Farmer/products";
    }

}
