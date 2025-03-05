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

import DJ.MyDigital.Model.MProduct;
import DJ.MyDigital.Model.Merchant;
import DJ.MyDigital.service.MProductService;
import DJ.MyDigital.service.MerchantService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/Merchant/products")
public class MProductController {

    @Autowired
    private MProductService productService;

    @Autowired
    private MerchantService merchantService;


    // New Product add by Merchant
    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("product", new MProduct());
        return "Merchantproduct-form";
    }

    @PostMapping("/save")
    public String saveProduct(@ModelAttribute("product") MProduct product, HttpSession session) {
        Long merchantId = (Long) session.getAttribute("merchantId");

        if (merchantId != null) {
            Optional<Merchant> optionalMerchant = merchantService.getMerchantById(merchantId);
            if (optionalMerchant.isPresent()) {
                product.setMerchant(optionalMerchant.get());
                productService.saveProduct(product);
                return "redirect:/Merchant/MerchantHome";
            }
        }
        return "error";
    }

    
    // Update the Product BY Merchant
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        MProduct product = productService.getProductById(id);
        if (product != null) {
            model.addAttribute("product", product);
            return "MerchantEditProduct";
        }
        return "redirect:/Merchant/products";
    }

    @PostMapping("/update")
    public String updateProduct(@ModelAttribute MProduct updatedProduct) {
        MProduct existingProduct = productService.getProductById(updatedProduct.getId());

        if (existingProduct != null) {
            updatedProduct.setMerchant(existingProduct.getMerchant()); // Retain merchant reference
            productService.saveProduct(updatedProduct);
        }
        return "redirect:/Merchant/MerchantHome";
    }

    // Delete the Product 
    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/Merchant/MerchantHome";
    }


    // Add product by the Admin for Specific Product by any Merchant
    @GetMapping("/merchant/{merchantId}")
    public String getProductsByMerchant(@PathVariable Long merchantId, Model model) {
        Optional<Merchant> optionalMerchant = merchantService.getMerchantById(merchantId);
        if (optionalMerchant.isPresent()) {
            List<MProduct> products = productService.getProductsByMerchantId(merchantId);
            model.addAttribute("products", products);
            model.addAttribute("merchant", optionalMerchant.get());
            return "MerchantProductAdminUpdate";
        }
        return "error";
    }


    // Admin View of all products
    @GetMapping()
    public String listProducts(Model model) {
        List<MProduct> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "MerchantProductListAdmin";
    }

    // Admin Update single product
    @GetMapping("admin/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model) {
        MProduct product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "MerchantProductAdminUpdate";
    }

    @PostMapping("admin/update")
    public String updateAdminProduct(@ModelAttribute MProduct product) {
        MProduct existingProduct = productService.getProductById(product.getId());
        if (existingProduct != null) {
            product.setMerchant(existingProduct.getMerchant()); // Keep the merchant reference
            productService.updateProduct(product);
        }
        return "redirect:/Merchant/products";
    }

    // Admin Delete single product
    @GetMapping("admin/delete/{id}")
    public String deleteProductAdmin(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/Merchant/products";
    }
}
