import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import javax.xml.xpath.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class query_xml {
    public static void main(String[] args) {
        // Define scanner for user input from keyboard
        Scanner scanner = new Scanner(System.in);

        // Get minimum and maximum score for query
        float minScore = 0, maxScore = 0;
        System.out.println("Nhập ngưỡng điểm thấp:");
        minScore = scanner.nextFloat();
        System.out.println("Nhập ngưỡng điểm cao:");
        maxScore = scanner.nextFloat();

        // Swap if minimum score is higher than maximum
        if (minScore > maxScore){
            float t = minScore;
            minScore = maxScore;
            maxScore = t;
        }

        // Get the xml file name from fileName.txt which was print by query_db.java
        String filePath = "..\\..\\input\\fileName.txt";
        String fileName = "null.xml";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            fileName = reader.readLine();
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file: " + e.getMessage());
        }
        File inputFile = new File("..\\..\\XML\\"+fileName);
        System.out.println("File xml thực hiện truy vấn: " + fileName);
        System.out.println("Danh sách học sinh có điểm trung bình từ " + minScore + " đến " + maxScore +":");
        try {
            // Load the XML file
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputFile);

            // Create an XPath object
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();

            // Define the XPath expression to select the desired nodes
            String expressionName = "//Student/Name";
            String expressionDTB = "//Student/Score";

            // Evaluate the XPath expression and retrieve the matching nodes
            NodeList nodeListName = (NodeList) xpath.evaluate(expressionName, document, XPathConstants.NODESET);
            NodeList nodeListDTB = (NodeList) xpath.evaluate(expressionDTB, document, XPathConstants.NODESET);

            // Process the retrieved nodes
            for (int i = 0; i < nodeListName.getLength(); i++) {
                Node studentName = nodeListName.item(i);
                Node studentScore = nodeListDTB.item(i);
                String studentNameValue = studentName.getTextContent();
                float studentScoreValue = Float.parseFloat(studentScore.getTextContent());
                if (studentScoreValue >= minScore && studentScoreValue <= maxScore) System.out.println(studentNameValue);
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }
}
