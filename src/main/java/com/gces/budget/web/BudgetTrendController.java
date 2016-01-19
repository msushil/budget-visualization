package com.gces.budget.web;

import com.gces.budget.domain.dto.BudgetDTO;
import com.gces.budget.domain.entity.ExpenseBudget;
import com.gces.budget.domain.entity.IncomeBudget;
import com.gces.budget.service.UserService;
import com.gces.budget.service.budget.BudgetService;
import com.gces.budget.service.budget.analysis.BudgetAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpServerErrorException;

import java.security.Principal;
import java.util.List;

/**
 * Created by minamrosh on 1/15/16.
 */

@Controller
public class BudgetTrendController {

    private final Logger log = LoggerFactory.getLogger(BudgetTrendController.class);

    private BudgetService budgetService;

    private UserService userService;

    @Autowired
    public void setBudgetService(BudgetService budgetService){
        this.budgetService = budgetService;
    }

    @Autowired
    public void setUserService(UserService userService){
        this.userService = userService;
    }


    @RequestMapping(value="/budget-trend/income/{chart}")
    public String visualizeIncome(Principal principal, Model model, @PathVariable("chart") String chart){

        List<IncomeBudget> incomeBudgetList = budgetService.getAllIncomeBudgetSheets(userService.getCurrentUserId(principal));


        model.addAttribute("budgets",incomeBudgetList);

        if(chart.equalsIgnoreCase("barchart")) {
            model.addAttribute("pageTitle", "Barchart | Income | Budget Trend | Budget Visualization & Analysis Tool");
            model.addAttribute("sectionTitle", "Stacked Bar Chart");
            return "income-trend-barchart";
        }
        else if(chart.equalsIgnoreCase("linechart")){
            model.addAttribute("pageTitle","Pie Chart | Income | Budget Trend | Budget Visualization & Analysis Tool");
            model.addAttribute("sectionTitle", "Line Chart");
            return "income-trend-linechart";
        }
        else{
            throw new HttpServerErrorException(HttpStatus.BAD_REQUEST);
        }

    }

    @RequestMapping(value="/budget-trend/expense/{chart}")
    public String visualizeExpense(Principal principal, Model model,@PathVariable("chart") String chart){
        List<ExpenseBudget> expenseBudgets = budgetService.getAllExpenseBudgetSheetByUser(
                userService.getCurrentUserId(principal)
        );
        model.addAttribute("budgets",expenseBudgets);
        if(chart.equalsIgnoreCase("barchart")) {
            model.addAttribute("pageTitle", "Stacked Bar Chart | Expense Budget | Budget Trend | Budget Visualization & Analysis Tool");
            model.addAttribute("sectionTitle", "Stacked Bar Chart ");
            return "expense-trend-barchart";
        }
        else if(chart.equalsIgnoreCase("linechart")){
            model.addAttribute("pageTitle","Pie Chart | Expense Budget | Visualization | Budget Visualization & Analysis Tool");
            model.addAttribute("sectionTitle","Line Chart");
            return "expense-trend-linechart";
        }
        else{
            throw new HttpServerErrorException(HttpStatus.BAD_REQUEST);
        }

    }

    @RequestMapping(value = "/project-budget")
    public String projectBudget(Principal principal, Model model){
        List<BudgetDTO> budgetDTOs = budgetService.getAllTotalIncomeBudget(userService.getCurrentUserId(principal));
        BudgetAnalysisService budgetAnalysisService = new BudgetAnalysisService(budgetDTOs);
        double slope = budgetAnalysisService.getSlope();
        double intercept = budgetAnalysisService.getIntercept();
        int[] interval = budgetAnalysisService.getYearInterval();

        int x1 = 1;
        double y1 = intercept + (slope * x1);

        int x2 = interval.length;
        double y2 = intercept + (slope * x2);

        model.addAttribute("budgets",budgetDTOs);
        model.addAttribute("x1",x1);
        model.addAttribute("y1",y1);
        model.addAttribute("x2", x2);
        model.addAttribute("y2",y2);
        model.addAttribute("interval",interval);
        return "project-budget";

    }
}
