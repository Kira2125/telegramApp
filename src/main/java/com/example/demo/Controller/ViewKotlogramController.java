package com.example.demo.Controller;

import com.example.demo.Kotlogram.ViewKotlogramService;
import com.example.demo.Model.Activation;
import com.example.demo.Model.Phone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ViewKotlogramController {

    @Autowired
    private ViewKotlogramService viewKotlogramService;

    @GetMapping("/get-form")
    public String greetingForm(Model model) {
        model.addAttribute("phone", new Phone());
        return "intro";
    }

    @PostMapping("/number")
    public String greetingSubmit(@ModelAttribute Phone phone, Model model) {
        viewKotlogramService.sendVerificationCode(phone.getPhone());
        model.addAttribute("activation", new Activation());
        return "code";
    }


    @PostMapping("/code")
    public String greetingSubmit(@ModelAttribute Activation code, Model model, RedirectAttributes attributes) {
        viewKotlogramService.submitVerificationCode(code.getCode(), code.getPhone());
        attributes.addAttribute("phone", code.getPhone());
        return "redirect:/kotlogram/get-dialogs";
    }


}
