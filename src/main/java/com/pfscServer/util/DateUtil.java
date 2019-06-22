/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pfscServer.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author User
 */
public class DateUtil {
    
    public static String getDateString(LocalDateTime locDate, String divider) {
        String day = (locDate.getDayOfMonth() < 10 ? "0" : "") + locDate.getDayOfMonth(); 
        String month = (locDate.getMonthValue() < 10 ? "0" : "") + locDate.getMonthValue(); 
        return  day + divider + month + divider + locDate.getYear();
    }
    
    public static LocalDateTime convertToDate(String date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(date.trim());
        } catch (ParseException pe) {
            return null;
        }
        return LocalDateTime.parse(date, formatter);
    }
}
