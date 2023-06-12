package com.example.demo.controllers;

import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.models.Student;
import com.example.demo.models.StudentRepository;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class studentController {
    
    @Autowired
    private StudentRepository studentRepo;

    @PostMapping("/buttonClicked")
    // @ResponseBody
    public String handleButtonClick(@RequestParam("buttonValue") String buttonValue, Model model) {
        if (buttonValue.equals("Add")) {
            // Button 1 was clicked
            return "students/add";
        } 
        List<Student> students = studentRepo.findAll();
        System.out.println("List generated");
        // end of database call
        model.addAttribute("stu", students);

        if (buttonValue.equals("Delete")) {
            // Button 2 was clicked
            return "students/del";
        } else if(buttonValue.equals("showAll")){
            return "students/showAll";
        }else if(buttonValue.equals("Modify")){
            return "students/modify";
        }

        else {
            // Neither button was clicked or invalid button value
            setRelativeHW(model, studentRepo.findAll());
            return "students/boxGraph";
        }
    }

    private void setRelativeHW(Model model, List<Student> stuList){
        double maxH=Double.parseDouble(stuList.get(0).getHeight());
        double maxW=Double.parseDouble(stuList.get(0).getWeight());
        for(Student s:stuList){
            double tempH=Double.parseDouble(s.getHeight());
            double tempW=Double.parseDouble(s.getWeight());
            if(tempH>maxH){
                maxH=tempH;
            }
            if(tempW>maxW){
                maxW=tempW;
            }
        }
        for(Student s:stuList){
            double relativeH=Double.parseDouble(s.getHeight())*90/maxH;
            double relativeW=Double.parseDouble(s.getWeight())*500/(maxW*stuList.size());
            s.setRelativeH(""+(Math.ceil(relativeH)-4));
            s.setRelativeW(""+(Math.ceil(relativeW)-4));
            s.setMarTop(""+(100-relativeH));
            System.out.println("reH: "+s.getRelativeH());
            System.out.println("reW: "+s.getRelativeW());
            System.out.println("martop: "+s.getMarTop());
        }
        model.addAttribute("stu", stuList);
        
    }

    @GetMapping("/students/show")
    //@GetMapping("")
    public String getAllStudents(Model model){
        System.out.println("Getting all students");
        // get all users from database
        List<Student> students = studentRepo.findAll();
        System.out.println("List generated");
        // end of database call
        model.addAttribute("stu", students);
        return "students/showAll";
    }

    @GetMapping("/students/find")
    //@ResponseBody
    public String delStudent(@RequestParam("toDel") String sid,Model model) {
        if(sid==""){
        model.addAttribute("msg", "Error: input is null");
            return "students/invalidInput";
        }
        List<Student> l=studentRepo.findBySid(sid);
        if(l.size()==0){
            model.addAttribute("msg", "Error: Student not exist");
            return "students/invalidInput";
        }
        studentRepo.delete(l.get(0));
        // return "students/deleted";
        model.addAttribute("msg", "delected");
        return "students/success";
    }


    @GetMapping("/students/startUpdate")
    //@ResponseBody
    public String modStudent(@RequestParam("toMod") String sid,Model model) {
        if(sid==""){
        model.addAttribute("msg", "Error: input is null");
            return "students/invalidInput";
        }
        List<Student> l=studentRepo.findBySid(sid);
        if(l.size()==0){
            model.addAttribute("msg", "Error: Student not exist");
            return "students/invalidInput";
        }
        Student orig=l.get(0);
        Student s=new Student("Origin SID: "+orig.getSid(), 
                            "Origin Name: "+orig.getName(),
                            "Origin Weight(kg): "+orig.getWeight(),
                            "Origin Height(cm): "+ orig.getHeight(),
                            "Origin Hair Color: "+orig.getHairColor(), 
                            "Origin GPA: "+orig.getGpa());
        model.addAttribute("student", s);
        model.addAttribute("originSID", sid);
        return "students/modifyForm";
    }

     @PostMapping("/students/update")
    public String updateUser(@RequestParam Map<String, String> newstudent, HttpServletResponse response,Model model){
        String newSid=newstudent.get("sid");
        String newName = newstudent.get("name");
        String newWeight = newstudent.get("weight");
        String newHeight = newstudent.get("height");
        String newHairColor = newstudent.get("hairColor");
        String newGpa = newstudent.get("gpa");
        String[] userInput={newSid,newName,newWeight,newHeight,newHairColor,newGpa};
        
        System.out.println("got all info");
        if(!areNum(model,userInput)){
            return "students/invalidInput";
        }
        System.out.println("is good input");
        if(studentRepo.findBySid(newSid).size()!=0){
            model.addAttribute("msg", "Error: exiting SID");
            return "students/invalidInput";
        }
        System.out.println("Update: is good sid");

        Student existingStudent=studentRepo.findBySid(newstudent.get("origSID")).get(0);
        if(!userInput[0].equals("")){
            existingStudent.setSid(newSid);
        }
        if(!userInput[1].equals("")){
            existingStudent.setName(newName);
        }
        if(!userInput[2].equals("")){
            existingStudent.setWeight(newWeight);
        }
        if(!userInput[3].equals("")){
            existingStudent.setHeight(newHeight);
        }
        if(!userInput[4].equals("")){
            existingStudent.setHairColor(newHairColor);
        }
        if(!userInput[5].equals("")){
            existingStudent.setGpa(newGpa);
        }
        System.out.println("student updated");
        studentRepo.save(existingStudent);
        System.out.println("student saved");
        response.setStatus(201);
        model.addAttribute("msg", "modified");
        return "students/success";
    }

    @PostMapping("/students/add")
    public String addUser(@RequestParam Map<String, String> newstudent, HttpServletResponse response,Model model){
        System.out.println("ADD user");
        String newSid=newstudent.get("sid");
        String newName = newstudent.get("name");
        String newWeight = newstudent.get("weight");
        String newHeight = newstudent.get("height");
        String newHairColor = newstudent.get("hairColor");
        String newGpa = newstudent.get("gpa");
        System.out.println("got all info");
        if(!isGoodInput(model,newSid,newName, newWeight, newHeight, newHairColor, newGpa)){
            return "students/invalidInput";
        }
        System.out.println("is good input");
        if(studentRepo.findBySid(newSid).size()!=0){
            return "students/invalidInput";
        }
        System.out.println("is good sid");
        Student s=new Student(newSid, newName,newWeight,newHeight,newHairColor,newGpa);
        System.out.println("student created");
        studentRepo.save(s);
        System.out.println("student added");
        response.setStatus(201);
        //return "students/added";
        model.addAttribute("msg", "added");
        return "students/success";
    }

    private boolean isGoodInput(Model model,String newSid, String newName, String newWeight, String newHeight, String newHairColor, String newGpa){
        System.out.println("start checking isGoodInput");
        if(newSid.equals("") || newName.equals("") || newWeight.equals("")|| newHeight.equals("")|| newHairColor.equals("")|| newGpa.equals("")){
            model.addAttribute("msg", "Error: Some of the field is null");
            return false;
        }

        String[] toCheck={newSid,newWeight,newHeight,newGpa};
        String[] row={"Sid","Weight","Height","Gpa"};
        String message="";
        Boolean allisNum=true;
        for(int k=0;k<4;k++){
            if(!isNumber(toCheck[k])){
                message+=row[k]+", ";
                allisNum=false;
            }
        }

        if(!allisNum){
            message="Error: "+message.substring(0, message.length()-2)+" is/are not a number ";
            model.addAttribute("msg", message);
            return false;
        }

        List<Student> l=studentRepo.findBySid(newSid);
        if(l.size()!=0){
            model.addAttribute("msg", "student is already exist");
            return false;
        }
        
        return true;
    }

    private  boolean isNumber(String str) { 
        try {  
            Double.parseDouble(str);  
            return true;
        } 
        catch(NumberFormatException e){  
            return false;  
        }   
    }

    private boolean areNum(Model model,String[] userInput){
        int[] jump={0,2,3,5};
        String[] row={"Sid","Weight","Height","Gpa"};
        String message="";
        Boolean allisNum=true;
        for(int k=0;k<4;k++){
            if(!userInput[jump[k]].equals("")){
                if(!isNumber(userInput[jump[k]])){
                    message+=row[k]+", ";
                    allisNum=false;
                }
            }
        }
        if(!allisNum){
            message="Error: "+message.substring(0, message.length()-2)+" is/are not a number ";
            model.addAttribute("msg", message);
            return false;
        }
        return true;
    }
}
