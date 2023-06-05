import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class query_db {
    public static String dbName;            // Store database name
    public static String schoolName;        // Store school name
    public static String schoolYear;        // Store school year
    public static String studentRating;     // Store student rating
    public static void main(String[] args) {
        // Define scanner for user input from keyboard
        Scanner scanner = new Scanner(System.in);

        // Get database name
        System.out.println("Nhập tên database:");
        dbName = scanner.nextLine();

        // Get school name
        System.out.println("Nhập tên trường:");
        schoolName = scanner.nextLine();

        // Get school year
        System.out.println("Nhập năm học");
        schoolYear = scanner.nextLine();

        // Get student rating
        System.out.println("Nhập xếp loại:");
        studentRating = scanner.nextLine();

        // Output file name and path for future use
        String fileName = dbName+"_"+schoolName+"_"+schoolYear+"_"+studentRating+".xml";
        String filePath = "..\\..\\Input\\fileName.txt";

        // Write output file name to txt file for future use
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(fileName);
        }catch (IOException e) {
            System.out.println("An error occurred while writing to the file: " + e.getMessage());
        }

        // Query to get student list
        String query = "SELECT concat(hs.ho,\" \",hs.ten) as HoTen, hs.ntns as NTNS, hoc.diemtb as DIEMTB, hoc.xeploai as XepLoai, hoc.kqua as KetQua " +
                "FROM hoc join hs on hoc.mahs = hs.mahs join truong on hoc.matr = truong.matr " +
                "WHERE truong.tentr = '" + schoolName + "'and hoc.namhoc =" + schoolYear +" and " + "hoc.xeploai = '" + studentRating + "'";

        // Start count execute time
        long timeStart = System.currentTimeMillis();

        //Define new database connection
        ConnectJDBC connectJDBC = new ConnectJDBC();

        try (Connection conn = connectJDBC.connect()){

            //Start statement query
            Statement statement = conn.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery(query);
            FileWriter fileWriter = new FileWriter("..\\..\\XML\\"+dbName+"_"+schoolName+"_"+schoolYear+"_"+studentRating+".xml");

            // Create a new XML document
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();

            // Create the root element for XML
            Element rootElement = document.createElement("query_db");
            document.appendChild(rootElement);

            while (resultSet.next()){
                // Retrieve student information from the result set
                String studentName = resultSet.getString("HoTen");
                String studentBirth = resultSet.getString("NTNS");
                float studentScore = resultSet.getFloat("DIEMTB");
                String studentGPA = resultSet.getString("XepLoai");
                String studentResult = resultSet.getString("KetQua");

                System.out.println(studentName + " - " + studentBirth + " - " + studentScore + " - " + studentGPA + " - " + studentResult);

                // Create a new XML element for the student
                Element rowElement = document.createElement("Student");

                // Create child elements for each attribute of the student
                Element column1Element = document.createElement("Name");
                column1Element.appendChild(document.createTextNode(studentName));
                rowElement.appendChild(column1Element);

                Element column2Element = document.createElement("Birthday");
                column2Element.appendChild(document.createTextNode(studentBirth));
                rowElement.appendChild(column2Element);

                Element column3Element = document.createElement("Score");
                column3Element.appendChild(document.createTextNode(Float.toString(studentScore)));
                rowElement.appendChild(column3Element);

                Element column4Element = document.createElement("Rating");
                column4Element.appendChild(document.createTextNode(studentGPA));
                rowElement.appendChild(column4Element);

                Element column5Element = document.createElement("Result");
                column5Element.appendChild(document.createTextNode(studentResult));
                rowElement.appendChild(column5Element);

                rootElement.appendChild(rowElement);
            }

            // Create the XML transformer and set formatting options
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{https://xml.apache.org/xslt}indent-amount", "4");

            // Write the XML document to the file
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(fileWriter);
            transformer.transform(source, result);
            System.out.println("Data exported to XML successfully.");

            // Calculate and print the elapsed time
            long timeEnd = System.currentTimeMillis();
            float sec = (timeEnd - timeStart) / 1000F;
            System.out.println("Elapsed time: " + sec + " seconds");

        } catch (SQLException | IOException | ParserConfigurationException | TransformerException e){
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    /*
    Void to connect to database
     */
    public static class ConnectJDBC {
        private final String hostName = "localhost:3306";   // Replace this with host name
        private final String username = "root";             // Replace this with username
        private final String password = "IMainAkali1";      // Replace this with user password
        private final String connectionURL = "jdbc:mysql://" + hostName +"/" + dbName +
                "?&rewriteBatchedStatements=true";
        public Connection connect() {
            Connection conn = null;
            try {
                conn = DriverManager.getConnection(connectionURL, username, password);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return conn;
        }
    }
}