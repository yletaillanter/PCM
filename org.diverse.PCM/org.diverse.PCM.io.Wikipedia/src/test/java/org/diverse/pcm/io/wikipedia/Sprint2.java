package org.diverse.pcm.io.wikipedia;

import org.diverse.pcm.api.java.PCM;
import org.diverse.pcm.io.wikipedia.export.PCMModelExporter;
import org.diverse.pcm.io.wikipedia.export.WikiTextExporter;
import org.diverse.pcm.io.wikipedia.pcm.Page;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static scala.collection.JavaConversions.*;

import java.io.*;
import java.util.List;

/**
 * Created by yoannlt on 09/01/15.
 */
public class Sprint2 {

    ParserTest parser = new ParserTest();
    WikipediaPageMiner miner = new WikipediaPageMiner();

    File folder = new File("../org.diverse.PCM.io.Wikipedia/output/wikitext_sprint2");
    File[] listOfFiles = folder.listFiles();

    String filePath = "resources/list_of_PCMs_sprint2.txt";

    @Test
    public void PCM() {
        try {
            BufferedReader buff = new BufferedReader(new FileReader(filePath));
            try {
                String line;
                while ((line = buff.readLine()) != null) {

                    System.out.println(line);
                    System.out.println("...");

                    // Parse article from Wikipedia
                    String code = miner.getPageCodeFromWikipedia(line);
                    String preprocessedCode = miner.preprocess(code);
                    //writePreprocessed(line, preprocessedCode); // save preProcessed
                    Page page = miner.parse(preprocessedCode);

                    // Enregistrement du PCM ancienne version (html)
                    parser.writeToPCMOld(line, page);

                    // Enregistrement du PCM (JSON)
                    parser.writeToPCM(line,page);

                    // PCM list for wikitext
                    PCMModelExporter pcmExporter = new PCMModelExporter();
                    List<PCM> pcms = seqAsJavaList(pcmExporter.export(page));
                    assertFalse(pcms.isEmpty());

                    // Transform a list of PCM models into wikitext (markdown language for Wikipedia articles)
                    // Ajout du StringBuilder pour avoir un seul fichier
                    WikiTextExporter wikitextExporter = new WikiTextExporter();
                    StringBuilder wikitextFinal = new StringBuilder(line);
                    for (PCM pcm : pcms) {
                        String wikitext = wikitextExporter.toWikiText(pcm);
                        wikitextFinal.append(wikitext);
                        //assertNotNull(wikitext);
                    }
                    // Sauvegarde du WikiText
                    writeWikiText(line,wikitextFinal);

                    // Parse wikitext to PCM again
                    for (int i = 0; i < listOfFiles.length; i++) {
                        if (listOfFiles[i].isFile()) {
                            try {
                                //String FILE_PATH = listOfFiles[i].getPath();
                                //System.out.println(listOfFiles[i].getName().substring(0, listOfFiles[i].getName().length() - 4));
                                /**
                                 * A finir
                                 */

                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                        }
                    }

                }
            } finally {
                buff.close();
            }
        } catch (IOException ioe) {
            System.out.println("Erreur --" + ioe.toString());
        }
    }


    public void writeWikiText(String title, StringBuilder wikitext){
        FileWriter writer;

        try {
            writer = new FileWriter("output/wikitext/" + title.replaceAll(" ", "_") + ".txt");
            writer.write(wikitext.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
