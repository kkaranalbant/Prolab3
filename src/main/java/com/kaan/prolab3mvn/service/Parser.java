/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.prolab3mvn.service;

import com.kaan.prolab3mvn.model.Article;
import com.kaan.prolab3mvn.model.Author;
import com.kaan.prolab3mvn.model.Graph;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import static org.apache.poi.ss.usermodel.CellType.FORMULA;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author kaan
 */
public class Parser {

    Parser() {

    }

    public void parse(Graph graph, String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath); Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            // Skip the header row
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                //String orcid = getCellValue(row.getCell(0));
                String doi = doNameValidation(getCellValue(row.getCell(1)));
                //String authorPosition = getCellValue(row.getCell(2));
                String authorName = doNameValidation(getCellValue(row.getCell(3)));
                String coauthors = doNameValidation(getCellValue(row.getCell(4)));
                String paperTitle = doNameValidation(getCellValue(row.getCell(5)));
                
                
                
                Article article = new Article();
                article.setDoi(doi);
                article.setTitle(paperTitle);

                Author mainAuthor = getOrCreateAuthor(graph, authorName);
                mainAuthor.getArticles().add(article);
                // Process coauthors
                if (coauthors != null && !coauthors.isEmpty()) {
                    String[] coauthorNames = coauthors.split(",");
                    for (String coauthorName : coauthorNames) {
                        coauthorName = coauthorName.trim();
                        Author coauthor = getOrCreateAuthor(graph, coauthorName);
                        coauthor.getArticles().add(article);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Author getOrCreateAuthor(Graph graph, String authorName) {
        if (graph.getGraph().containsKey(authorName)) {
            return graph.getGraph().get(authorName).getAuthor();
        } else {
            Author newAuthor = new Author();
            newAuthor.setName(authorName);
            newAuthor.setArticles(new ArrayList<>());
            graph.getGraph().put(authorName, new Graph.Node(newAuthor));
            return newAuthor;
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    private String doNameValidation(String name) {
        StringBuilder sb = new StringBuilder();
        for (char character : name.toCharArray()) {
            if (character == '\'' || character == ']' || character == '[') {
                continue;
            }
            sb.append(character);
        }
        return sb.toString();
    }

}
