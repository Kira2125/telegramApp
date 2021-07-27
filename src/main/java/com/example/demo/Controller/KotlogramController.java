package com.example.demo.Controller;

import com.example.demo.Kotlogram.KotlogramService;
import com.example.demo.Kotlogram.ViewKotlogramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("kotlogram")
public class KotlogramController {
    @Autowired
    private KotlogramService kotlogramService;

    @Autowired
    private ViewKotlogramService viewKotlogramService;

    @GetMapping("/{phone}/send")
    public void sendMessage(@PathVariable String phone) {
        kotlogramService.sendMessage(phone);
    }

    @GetMapping("/{phone}/get-dialogs")
    @ResponseBody
    public String getLastDialogs(@PathVariable String phone) {
        StringBuilder stringBuilder = kotlogramService.getRecentConversationList(phone);
        return stringBuilder.toString();
    }




    @GetMapping("/get-dialogs")
    public String greetingForm(@RequestParam String phone) {
        StringBuilder stringBuilder = viewKotlogramService.getRecentConversationList(phone);
        return stringBuilder.toString();
    }
}
