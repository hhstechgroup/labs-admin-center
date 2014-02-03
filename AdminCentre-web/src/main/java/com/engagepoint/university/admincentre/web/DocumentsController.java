package com.engagepoint.university.admincentre.web;

/**
 * @author artem.lysenko
 */
import java.io.Serializable;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@ManagedBean (name = "documentsController")
public class DocumentsController implements Serializable {

    private static final Logger logger = Logger.getLogger(DocumentsController.class.getName());
    private TreeNode root;
    private Document selectedDocument;

    public DocumentsController() {
        root = new DefaultTreeNode("root", null);

        //Nodes
        TreeNode file1 = new DefaultTreeNode(new Document("File1", "-", "File"), root);
        TreeNode file2 = new DefaultTreeNode(new Document("File2", "-" ,"File"), root);
        TreeNode file3 = new DefaultTreeNode(new Document("File3", "-", "File"), root);

        //SubNodes of file1
        TreeNode subFile1_1 = new DefaultTreeNode(new Document("SubFile1.1", "-","SubFile"), file1);
        TreeNode subFile1_2 = new DefaultTreeNode(new Document("SubFile1.2", "-", "SubFile"), file1);

        //Keys in file1 including the file1's subfiles
        TreeNode key1_1_1 = new DefaultTreeNode("String", new Document("Key1.1.1",  "key1.1 value","String"), subFile1_1);
        TreeNode key1_1_2 = new DefaultTreeNode("Boolean", new Document("Key1.1.2", "true", "Boolean"), subFile1_1);
        TreeNode key1_2_1 = new DefaultTreeNode("Int", new Document("Key1.2.1", "123456789", "Int"), subFile1_2);

        //Keys in file2
        TreeNode key2_1 = new DefaultTreeNode("Byte Array", new Document("Key2.1", "??? view of Byte Array", "Byte[]"), file2);
        TreeNode key2_2 = new DefaultTreeNode("Double", new Document("Key2.2", "9999999999.9", "Double"), file2);
        TreeNode key2_3 = new DefaultTreeNode("Float", new Document("Key2.3", "0.123", "Float"), file2);

        //file3
        TreeNode subFile3_1 = new DefaultTreeNode(new Document  ("SubFile3.1", "-", "SubFile"), file3);
        TreeNode subFile3_1_1 = new DefaultTreeNode(new Document("SubFile3.1.1", "-", "SubFile"), subFile3_1);
        TreeNode subFile3_1_2 = new DefaultTreeNode(new Document("SubFile3.1.2", "-", "SubFile"), subFile3_1);
        TreeNode subFile3_1_3 = new DefaultTreeNode(new Document("SubFile3.1.3", "-", "SubFile"), subFile3_1);

        TreeNode key3_1_1_1 = new DefaultTreeNode("Long", new Document("Key3.1.1.1", "9999999999", "Long"), subFile3_1_1);
        TreeNode key3_1_1_2 = new DefaultTreeNode("Int",  new Document("Key3.1.1.2", "123456789", "Int"), subFile3_1_1);

        TreeNode key3_1_2_1 = new DefaultTreeNode("String", new Document("Key3.1.2.1", "James Gosling", "String"), subFile3_1_2);
        TreeNode key3_1_2_2 = new DefaultTreeNode("String", new Document("Key3.1.2.2", "Linus Torvalds", "String"), subFile3_1_2);

        TreeNode key3_1_3_1 = new DefaultTreeNode("Float",  new Document("Key3.1.3.1", "0.12345", "Float"), subFile3_1_3);
        TreeNode key3_1_3_2 = new DefaultTreeNode("Double", new Document("Key3.1.3.2", "0.123456789", "Double"), subFile3_1_3);
    }

    public TreeNode getRoot() {
        return root;
    }

    public Document getSelectedDocument() {
        return selectedDocument;
    }

    public void setSelectedDocument(Document selectedDocument) {
        this.selectedDocument = selectedDocument;
    }
}