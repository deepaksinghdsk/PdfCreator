package com.example.PdfCreator.ui.home;

public class PDFDoc {
    //ArrayList<PDFDoc> pdfDocs = new ArrayList<>();
    private String name, path;

    void setName(String name) {
        this.name = name;
    }

    void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    /*void setPdfDocs(ArrayList<PDFDoc> doc) {
        pdfDocs.addAll(doc);
    }*/

    /*ArrayList<PDFDoc> getDocs() {
        return pdfDocs;
    }*/
}
