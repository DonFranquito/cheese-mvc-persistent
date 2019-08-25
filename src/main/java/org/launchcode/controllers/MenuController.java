package org.launchcode.controllers;

import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


import javax.validation.Valid;

@Controller
@RequestMapping(value = "menu")
public class MenuController {

    //data objects

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private CheeseDao cheeseDao;

    //handlers

    @RequestMapping(value = "")
    public String index(Model model){
        model.addAttribute("title","Menus");
        model.addAttribute("Menus",menuDao.findAll());
        return "menu/index";
    }

    @RequestMapping(value="add", method = RequestMethod.GET)
    public String add(Model model){
        model.addAttribute("title","Add Menu");
        model.addAttribute( new Menu());
        return "menu/add";

    }

    @RequestMapping(value="add",method = RequestMethod.POST)
    public String add(Model model, @ModelAttribute @Valid Menu menu, Errors errors){

        if (errors.hasErrors()){
            model.addAttribute("title","Add Menu");
            return "menu/add";
        }

        menuDao.save(menu);

        return "redirect:view/" + menu.getId();
    }

    @RequestMapping(value="view/{menuId}", method=RequestMethod.GET)
    public String viewMenu(Model model, @PathVariable int menuId){
        Menu menu = menuDao.findOne(menuId);
        model.addAttribute("title", menu.getName());
        model.addAttribute("cheeses",menu.getCheeses());
        model.addAttribute("menuId",menu.getId());
        return "menu/view";

    }

    @RequestMapping(value="add-item/{menuId}", method=RequestMethod.GET)
    public String addItem(Model model, @PathVariable int menuId){
        Menu menu = menuDao.findOne(menuId);
        AddMenuItemForm form = new AddMenuItemForm(menu,cheeseDao.findAll());
        model.addAttribute("form",form);
        model.addAttribute("title","Add item to menu: " + menu.getName());
        return "menu/add-item";
    }

    @RequestMapping(value = "add-item", method = RequestMethod.POST)
    public String addItem(Model model, @ModelAttribute @Valid AddMenuItemForm form, Errors errors){

        if (errors.hasErrors()){
            model.addAttribute("form",form);
            return "menu/add-item";
        }
        Cheese aCheese = cheeseDao.findOne(form.getCheeseId());
        Menu aMenu = menuDao.findOne(form.getMenuId());
        aMenu.addItem(aCheese);
        menuDao.save(aMenu);
        return "redirect:/menu/view/" + aMenu.getId();
    }
}
