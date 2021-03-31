/*
 * Copyright(c) 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sastix.cms.common.services.htmltopdf;

import com.sastix.cms.common.dataobjects.htmltopdf.Param;
import com.sastix.cms.common.dataobjects.htmltopdf.page.PageType;
import com.sastix.cms.common.services.htmltopdf.config.HtmlToPdfConfiguration;
import com.sastix.cms.common.services.htmltopdf.config.HtmlToPdfConfig;
import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {HtmlToPdfConfiguration.class,PdfBuilder.class})
public class PdfTest {

    @Autowired
    PdfBuilder pdfBuilder;

    @Autowired
    HtmlToPdfConfig htmlToPdfConfig;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testCommand() throws Exception {
        Pdf pdf = pdfBuilder.build();
        pdf.addToc();
        pdf.addParam(new Param("--enable-javascript"), new Param("--html-header", "file:///example.html"));
        pdf.addPage("http://www.google.com", PageType.url);
        Assert.assertThat("command params should contain the --enable-javascript and --html-header", pdf.getCommand(), containsString("--enable-javascript --html-header file:///example.html"));
    }

    @Test
    public void findExecutable() throws Exception {
        assertThat("executable should be /usr/bin/wkhtmltopdf", htmlToPdfConfig.findExecutable(), containsString("/usr/bin/wkhtmltopdf"));
    }

    @Test
    public void testPdfFromStringTo() throws Exception {

        // GIVEN an html template containing special characters that java stores in utf-16 internally
        Pdf pdf = pdfBuilder.build();
        pdf.addPage("<html><head><meta charset=\"utf-8\"></head><h1>Müller</h1></html>", PageType.htmlAsString);

        String tempFolder = temporaryFolder.newFolder().getPath();
        pdf.saveAs(tempFolder+"/output.pdf");

        // WHEN
        byte[] pdfBytes = pdf.getPDF();

        PDFParser parser = new PDFParser(new RandomAccessBufferedFileInputStream(new ByteArrayInputStream(pdfBytes)));

        // that is a valid PDF (otherwise an IOException occurs)
        parser.parse();
        PDFTextStripper pdfTextStripper = new PDFTextStripper();
        String pdfText = pdfTextStripper.getText(new PDDocument(parser.getDocument()));

        assertThat("document should contain the creditorName", pdfText, containsString("Müller"));
    }

    final int numberOfTasks = 1500;
    CountDownLatch latch = new CountDownLatch(numberOfTasks);
    ConcurrentMap<Integer,Long> cmap = new ConcurrentHashMap<>();

    @Test
    public void performanceTest() throws InterruptedException {
        int NTHREDS = 30;//the lesser the threads, the completion time increases. At least 15 threads for better performance on my laptop
        ExecutorService executor = Executors.newFixedThreadPool(NTHREDS);
        long start = DateTime.now().getMillis();
        for (int i = 0; i < numberOfTasks; i++) {
            Runnable worker = new PdfRunnable(i,"<html><head><meta charset=\"utf-8\"></head><h1>Müller</h1></html>");
            executor.execute(worker);
        }
        try {
            latch.await();
        } catch (InterruptedException E) {
            // handle
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        assertEquals(cmap.size(), numberOfTasks);
        long passed = DateTime.now().getMillis() - start;
        log.info("Millis passed: "+passed);
        log.info("Seconds passed: "+(double)passed/1000);
    }

    class PdfRunnable implements Runnable{
        String html;
        Integer id;
        public PdfRunnable(Integer id,String html) {
            this.html = html;
            this.id = id;
        }

        @Override
        public void run() {
            Pdf pdf = pdfBuilder.build();
            pdf.addPage("<html><head><meta charset=\"utf-8\"></head><h1>Müller</h1></html>", PageType.htmlAsString);

            // WHEN
            try {
                byte[] pdfBytes = pdf.getPDF();
                assertThat(pdfBytes.length,greaterThan(1));
                cmap.put(id, DateTime.now().getMillis());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            latch.countDown();
        }
    }
}
