<%@ page import="java.io.File" %>
<%@ page import="java.nio.file.Files" %>
<%@ page import="java.nio.file.Paths" %>
<%@ page import="java.util.*" %>
<%@ page import="classes.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Home</title>
    <!-- CSS only -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.0/css/bootstrap.min.css" integrity="sha384-9aIt2nRpC12Uk9gS9baDl411NQApFmC26EwAOH8WgZl5MYYxFfc+NcPb1dKGj7Sk" crossorigin="anonymous">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <!-- JS, Popper.js, and jQuery -->
    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js" integrity="sha384-DfXdz2htPH0lsSSs5nCTpuj/zy4C+OGpamoFVy38MVBnE+IbbVYUew+OrCXaRkfj" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js" integrity="sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo" crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.0/js/bootstrap.min.js" integrity="sha384-OgVRvuATP1z7JjHLkuOU7Xw704+h835Lr+6QL9UvYjZE3Ipu6Tp75j7Bh/kR0JKI" crossorigin="anonymous"></script>

  </head>
  <body>
  <%!
      //Generazione di una lista contenente tutti i generi di partenza
      public String[] getFilesList(String path){
          File folder = new File(path + "/cocos_genres");
          File[] listOfFiles = folder.listFiles();
          String[] files= new String[19];

          int i=0;
          for (File file : listOfFiles) {
              if (file.isFile()) {

                  files[i]=file.getName();
                  i++;
              }
          }
          return files;
      }
  %>
  <%

      String PROJECT_PATH = application.getRealPath("/").replace('\\', '/'); //ottenimento del path del progetto
      String genereA = request.getParameter("genereA"); //parametro del primo genere di input
      String genereB = request.getParameter("genereB"); //parametro del secondo genere di input

      String[] files= getFilesList(PROJECT_PATH);

      //Creazione delle due select HTML
      String defaultOpt="<option value=\"\" selected disabled>Seleziona un valore</option>";
      String optionsA = defaultOpt;
      String optionsB = defaultOpt;
      for(String s: files){
          optionsA += "<option value=\""+s+"\"" + (genereA!=null && genereA.equals(s)? "selected": "") + ">"+s.replace("-", " ").replace("r b", "r&b")+"</option>";
          optionsB += "<option value=\""+s+"\"" + (genereB!=null && genereB.equals(s)? "selected": "") + ">"+s.replace("-", " ").replace("r b", "r&b")+"</option>";
      }

  %>
    <div class="container-fluid">
        <br />
        <div class="container justify-content-center">
            <form action="index.jsp" method="post">
                <label>Seleziona i due generi da combinare: </label>
                <div class="form-group row align-items-center">
                    <div class="col-auto mb-2">
                        <select class="browser-default custom-select" name="genereA" id="genereA" required>
                            <%=optionsA%>
                        </select>
                    </div>

                    <div class="col-auto mb-2">
                        <select class="browser-default custom-select" name="genereB" id="genereB" required>
                            <%=optionsB%>
                        </select>
                    </div>
                    <div class="col-auto mb-2">
                        <input id="combinaButton" type="submit" class="btn btn-primary" value="Combina">
                    </div>
                </div>
            </form>
            <br />
            <div class="card">


                <%
                    Map<String, Double> sortedGraduatoria = null;

                    if (genereA!=null  && genereB!=null){

                        String table=" <table class=\"table\">\n" +
                                "                    <thead>\n" +
                                "                        <tr>\n" +
                                "                            <th scope=\"col\">#</th>\n" +
                                "                            <th scope=\"col\">Titolo</th>\n" +
                                "                            <th scope=\"col\">Autore</th>\n" +
                                "                            <th scope=\"col\">Affidabilità</th>\n" +
                                "                            <th scope=\"col\">Ascolta</th>\n" +
                                "                        </tr>\n" +
                                "                    </thead>\n" +
                                "                    <tbody>";


                        String prototipo = genereA + "_" + genereB;
                        request.removeAttribute("genereA");
                        request.removeAttribute("genereB");

                        out.print("<div class=\"alert alert-primary\" role=\"alert\">" +
                                "Risultati della combinazione tra <a class=\"alert-link\">" +
                                prototipo.replace("-", " ").replace("_", " e ").replace("r b", "r&b") + "</a> </div>");


                        if(Files.exists(Paths.get(PROJECT_PATH + "/prototipi/" + prototipo))){
                            Recommender r = new Recommender(prototipo, PROJECT_PATH);

                            //chiamata per ottenere la graduatoria
                            sortedGraduatoria = r.getGraduatoria();

                            List<String> app = new ArrayList<String>(sortedGraduatoria.keySet());
                            int i=0;
                            double max_score = sortedGraduatoria.get(app.get(0));

                            //Generazione della tabella con i risultati
                            for(i=0; i<app.size() && !(i >= r.getMAX()) && !(sortedGraduatoria.get(app.get(i))==0.0); i++){
                                table += "<tr><th scope=\"row\">" + (i+1) + "</th>";

                                String[] nameAndArtist = app.get(i).split("!-!-!");
                                table += "<td>" + nameAndArtist[0] + "</td>";

                                table += "<td>" + (nameAndArtist.length==2? nameAndArtist[1].replace(" _", ","): "N/A") + "</td>";

                                double value = sortedGraduatoria.get(app.get(i));
                                int prop = (int) Math.round(value * 10 / max_score);

                                String rating = "";
                                String star = "&#9733;";
                                String emptyStar = "&#x2606;";
                                for (int j = 0; j<10; j++){
                                    rating += (j<prop)? star: emptyStar;
                                }

                                table += "<td>" + rating + "</td>";

                                table += "<td><a target=\"_blank\" href=\"https://www.youtube.com/results?search_query=" + nameAndArtist[0].replace(" ", "+")
                                        + "+" + nameAndArtist[1].replace(" ", "+") + "\">" +
                                        "<i class=\"material-icons\">contactless</i>" +
                                        "</a></td>";
                            }

                            out.print(table);

                        }else {
                            out.print("<div class=\"alert alert-danger\" role=\"alert\">" +
                                    "Non ci sono contenuti raccomandabili in questa categoria.</div>");
                        }
                    }
                %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
  </body>
</html>
