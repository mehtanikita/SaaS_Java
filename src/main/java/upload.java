

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.appengine.repackaged.com.google.appengine.api.search.SearchServicePb.IndexMetadata.Storage;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet implementation class upload
 */
@WebServlet("/upload")
public class upload extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public upload() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		try{

		ServletContext application = getServletContext();
		String filePath = application.getRealPath(request.getParameter("image"));
		ImageAnnotatorClient vision = ImageAnnotatorClient.create();
		String fileName = "Images/dog1.jpg";
		System.out.println("-----------");
		System.out.println(fileName);
		System.out.println(filePath);
		System.out.println("-----------");
		//String fileName = "./resources/wakeupcat.jpg";

	      // Reads the image file into memory
	      Path path = Paths.get(fileName);
	      byte[] data = Files.readAllBytes(path);
	      ByteString imgBytes = ByteString.copyFrom(data);

	      // Builds the image annotation request
	      List<AnnotateImageRequest> requests = new ArrayList<>();
	      Image img = Image.newBuilder().setContent(imgBytes).build();
	      Feature feat = Feature.newBuilder().setType(Type.LABEL_DETECTION).build();
	      AnnotateImageRequest requestAnnotate =
	          AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
	      requests.add(requestAnnotate);

	      // Performs label detection on the image file
	      BatchAnnotateImagesResponse responseBatch = vision.batchAnnotateImages(requests);
	      List<AnnotateImageResponse> responses = responseBatch.getResponsesList();
	      
	      StringBuilder responseTags = new StringBuilder();
	      for (AnnotateImageResponse res : responses) {
	        if (res.hasError()) {
	          System.out.printf("Error: %s\n", res.getError().getMessage());
	          return;
	        }

	        for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
	          annotation
	              .getAllFields()
	              .forEach((k, v) -> responseTags.append("<td>" + v.toString() + "</td>\n"));
	          responseTags.append("</tr>\n<tr>");
	        }
	      }
	      response.setContentType("text/html");
	      PrintWriter out = response.getWriter();
	      out.println("<!doctype html>\n" + 
	      		"<html>\n" + 
	      		"   <body style=\"background-color:rgb(241, 245, 245);font-size: 20px;color:rgb(50, 128, 173);\"><center>\n" + 
	      		"      <table border = 1>\n" + 
	      		"          <caption style=\"font-size:50px;\">Detected labels</caption>\n" + 
	      		"          <tr>\n" + 
	      		"               <th> mid </th>\n" + 
	      		"               <th> Description </th>\n" + 
	      		"               <th> Score </th>\n" + 
	      		"               <th> Topicality </th>\n" + 
	      		"\n" + 
	      		"          </tr>\n" + 
	      		"\n" + 
	      		"        \n" + 
	      		"            <tr>\n" + 
	      		responseTags.toString() +
	      		"            </tr>\n" + 
	      		"         \n" + 
	      		"      </table>\n" + 
	      		"      </center>\n" + 
	      		"   </body>\n" + 
	      		"</html>");
	      
		}
		catch(Exception e) {
			System.out.println(e);
		}
		
	    }
	 
	
	
	
	
	
	
	
	
	
	
	
	    /*ByteString imageBytes = ByteString.readFrom(new FileInputStream(filePath));
	    Image image = Image.newBuilder().setContent(imageBytes).build();

	    Feature feature = Feature.newBuilder().setType(Type.LABEL_DETECTION).build();
	    AnnotateImageRequest requestAnnotate =
	        AnnotateImageRequest.newBuilder().addFeatures(feature).setImage(image).build();
	    List<AnnotateImageRequest> requests = new ArrayList<>();
	    requests.add(requestAnnotate);
	    
	    ImageAnnotatorClient client = ImageAnnotatorClient.create();
	    BatchAnnotateImagesResponse batchResponse = client.batchAnnotateImages(requests);
	    List<AnnotateImageResponse> imageResponses = batchResponse.getResponsesList();
	    AnnotateImageResponse imageResponse = imageResponses.get(0);
	    
	    System.out.println(filePath);
	    if (imageResponse.hasError()) {
	      System.out.println("Error: " + imageResponse.getError().getMessage());
	    }

	    for (EntityAnnotation annotation : imageResponse.getLabelAnnotationsList()) {
	      System.out.println(annotation.getDescription() + ": " + annotation.getScore());
	    }

	    client.close();
	    
//		QuickstartSample obj = new QuickstartSample();
//		try {
//			obj.main(filePath);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}*/
	

}

