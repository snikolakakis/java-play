package controllers.coreData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.execution_context.DatabaseExecutionContext;
import models.coreData.CoreGeografikesTheseisEntity;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import play.db.jpa.JPAApi;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static play.mvc.Results.badRequest;
import static play.mvc.Results.ok;

public class CoreGeografikesTheseisController  {
    private JPAApi jpaApi;
    private final WSClient ws;
    private DatabaseExecutionContext executionContext;

    @Inject
    public CoreGeografikesTheseisController(JPAApi jpaApi, DatabaseExecutionContext executionContext, WSClient ws) {
        this.jpaApi = jpaApi;
        this.executionContext = executionContext;
        this.ws=ws;
    }
    @SuppressWarnings({"Duplicates", "unchecked"})
    @BodyParser.Of(BodyParser.Json.class)
    public Result addCoreGeografikiThesi(final Http.Request request) throws IOException {
        JsonNode json =
                request.body().asJson();
        if (json == null) {
            return badRequest("Expecting Json data");
        } else {
            try {
                ObjectNode result = Json.newObject();
                CompletableFuture<JsonNode> addFuture = CompletableFuture.supplyAsync(() -> {
                            return jpaApi.withTransaction(entityManager -> {
                                ObjectNode add_result = Json.newObject();
                                Long user_id = json.findPath("user_id").asLong();
                                String code = json.findPath("code").asText();
                                String title = json.findPath("title").asText();
                                String epipedo = json.findPath("epipedo").asText();
                                CoreGeografikesTheseisEntity entity = new CoreGeografikesTheseisEntity();
                                String sqlUnique = "select * from CORE_GEOGRAFIKES_THESEIS cms where cms.CODE= '" + code + "'";
                                List<CoreGeografikesTheseisEntity> list = entityManager.createNativeQuery(sqlUnique, CoreGeografikesTheseisEntity.class).getResultList();
                                if (list.size() > 0) {
                                    add_result.put("status", "error");
                                    add_result.put("message", "Βρέθηκε εγγραφή με το ίδιο λεκτικό, προσπαθήστε ξανά");
                                    return add_result;
                                }
                                entity.setCode(code);
                                entity.setTitle(title);
                                entity.setEpipedo(epipedo);
                                entity.setStatus("DRAFT");
                                entity.setActive(1);
                                entityManager.persist(entity);
                                add_result.put("status", "success");
                                add_result.put("message", "Η καταχώρηση ολοκληρώθηκε με επιτυχία!");
                                add_result.put("user_id", user_id);
                                add_result.put("DO_ID", entity.getId());
                                add_result.put("system", "CORE_GEOGRAFIKES_THESEIS");
                                add_result.put("system_label_front", "ΓΕΩΓΡΑΦΙΚΕΣ ΘΕΣΕΙΣ");
                                return add_result;
                            });
                        },
                        executionContext);
                result = (ObjectNode) addFuture.get();
                return ok(result);
            } catch (Exception e) {
                ObjectNode result = Json.newObject();
                e.printStackTrace();
                result.put("status", "error");
                result.put("message", "Προβλημα κατα την καταχωρηση");
                return ok(result);
            }
        }
    }

    @SuppressWarnings({"Duplicates", "unchecked"})
    @BodyParser.Of(BodyParser.Json.class)
    public Result updateCoreGeografikiThesi(final Http.Request request) throws IOException {
        JsonNode json = request.body().asJson();
        if (json == null) {
            return badRequest("Expecting Json data");
        } else {
            try {
                ObjectNode result = Json.newObject();
                CompletableFuture<JsonNode> updateFuture = CompletableFuture.supplyAsync(() -> {
                            return jpaApi.withTransaction(entityManager -> {
                                ObjectNode update_result = Json.newObject();
                                Long user_id = json.findPath("user_id").asLong();
                                Long id = json.findPath("id").asLong();
                                String code = json.findPath("code").asText();
                                String title = json.findPath("title").asText();
                                String epipedo = json.findPath("epipedo").asText();
                                String status = json.findPath("status").asText();
                                Integer active = json.findPath("active").asInt();
                                CoreGeografikesTheseisEntity entity = entityManager.find(CoreGeografikesTheseisEntity.class, id);
                                if (entity == null) {
                                    update_result.put("status", "error");
                                    update_result.put("message", "Η εγγραφή που προσπαθείτε να ενημερώσετε δεν υπάρχει");
                                    return update_result;
                                }
                                String sqlUnique = "select * from CORE_GEOGRAFIKES_THESEIS cms where cms.CODE = '" + code + "' and ID != " + id;
                                List<CoreGeografikesTheseisEntity> list = entityManager.createNativeQuery(sqlUnique, CoreGeografikesTheseisEntity.class).getResultList();
                                if (list.size() > 0) {
                                    update_result.put("status", "error");
                                    update_result.put("message", "Βρέθηκε εγγραφή με το ίδιο λεκτικό,προσπαθήστε ξανά");
                                    return update_result;
                                }
                                entity.setCode(code);
                                entity.setTitle(title);
                                entity.setEpipedo(epipedo);
                                entity.setStatus(status);
                                entity.setActive(active);
                                entityManager.merge(entity);
                                update_result.put("status", "success");
                                update_result.put("message", "Η ενημέρωση ολοκληρώθηκε με επιτυχία!");
                                update_result.put("user_id", user_id);
                                update_result.put("DO_ID", entity.getId());
                                update_result.put("system", "CORE_GEOGRAFIKES_THESEIS");
                                update_result.put("system_label_front", "ΓΕΩΓΡΑΦΙΚΕΣ ΘΕΣΕΙΣ");
                                return update_result;
                            });
                        },
                        executionContext);
                result = (ObjectNode) updateFuture.get();
                return ok(result);
            } catch (Exception e) {
                ObjectNode result = Json.newObject();
                e.printStackTrace();
                result.put("status", "error");
                result.put("message", "Προβλημα κατα την καταχωρηση");
                return ok(result);
            }
        }
    }

    @SuppressWarnings({"Duplicates", "unchecked"})
    @BodyParser.Of(BodyParser.Json.class)
    public Result deleteCoreGeografikiThesi(final Http.Request request) throws IOException {
        JsonNode json = request.body().asJson();
        if (json == null) {
            return badRequest("Expecting Json data");
        } else {
            try {
                ObjectNode result = Json.newObject();
                CompletableFuture<JsonNode> deleteFuture = CompletableFuture.supplyAsync(() -> {
                            return jpaApi.withTransaction(entityManager -> {
                                ObjectNode delete_result = Json.newObject();
                                Long id = json.findPath("id").asLong();
                                Long user_id = json.findPath("user_id").asLong();
                                CoreGeografikesTheseisEntity entity = entityManager.find(CoreGeografikesTheseisEntity.class, id);
                                if (entity == null) {
                                    delete_result.put("status", "error");
                                    delete_result.put("message", "Η εγγραφή που προσπαθείτε να διαγράψετε δεν υπάρχει");
                                    return delete_result;
                                }
                                entityManager.remove(entity);
                                delete_result.put("status", "success");
                                delete_result.put("message", "Η διαγραφή ολοκληρώθηκε με επιτυχία!");
                                delete_result.put("user_id", user_id);
                                delete_result.put("DO_ID", entity.getId());
                                delete_result.put("system", "CORE_GEOGRAFIKES_THESEIS");
                                delete_result.put("system_label_front", "ΓΕΩΓΡΑΦΙΚΕΣ ΘΕΣΕΙΣ");
                                return delete_result;
                            });
                        },
                        executionContext);
                result = (ObjectNode) deleteFuture.get();
                return ok(result);
            } catch (Exception e) {
                ObjectNode result = Json.newObject();
                e.printStackTrace();
                result.put("status", "error");
                result.put("message", "Προβλημα κατα την διαγραφή");
                return ok(result);
            }
        }

    }

    @SuppressWarnings({"Duplicates", "unchecked"})
    public Result getCoreGeografikesTheseis(final Http.Request request) throws IOException, ExecutionException, InterruptedException {
        ObjectNode result = Json.newObject();
        JsonNode json = request.body().asJson();
        if (json == null) {
            return badRequest("Expecting Json data");
        } else {
            if (json == null) {
                result.put("status", "error");
                result.put("message", "Δεν εχετε αποστειλει εγκυρα δεδομενα.");
                return ok(result);
            } else {
                ObjectMapper ow = new ObjectMapper();
                HashMap<String, Object> returnList = new HashMap<String,Object>();
                String jsonResult = "";
                CompletableFuture<HashMap<String, Object>> getFuture = CompletableFuture.supplyAsync(() -> {
                            return jpaApi.withTransaction(
                                    entityManager -> {
                                        String orderCol = json.findPath("orderCol").asText();
                                        String descAsc = json.findPath("descAsc").asText();
                                        String id = json.findPath("id").asText();
                                        String code = json.findPath("code").asText();
                                        String title = json.findPath("title").asText();
                                        String epipedo = json.findPath("epipedo").asText();
                                        String start = json.findPath("start").asText();
                                        String limit = json.findPath("limit").asText();
                                        String sqlSearch = "select * from CORE_GEOGRAFIKES_THESEIS cms where 1=1 ";
                                        if (id != null && !id.equalsIgnoreCase("")) {
                                            sqlSearch += " and cms.ID like '%" + id + "%'";
                                        }
                                        if (code != null && !code.equalsIgnoreCase("")) {
                                            sqlSearch += " and cms.CODE like '%" + code + "%'";
                                        }
                                        if (title != null && !title.equalsIgnoreCase("")) {
                                            sqlSearch += " and cms.TITLE like '%" + title +"%'";
                                        }
                                        if (epipedo != null && !epipedo.equalsIgnoreCase("")) {
                                            sqlSearch += " and cms.EPIPEDO like '%" + epipedo + "%'";
                                        }
                                        List<CoreGeografikesTheseisEntity> listAll= (List<CoreGeografikesTheseisEntity>) entityManager.createNativeQuery(sqlSearch, CoreGeografikesTheseisEntity.class).getResultList();

                                        if (!orderCol.equalsIgnoreCase("") && orderCol != null) {
                                            sqlSearch += " order by " + orderCol + " " + descAsc;
                                        }else{
                                            sqlSearch += " order by id desc";
                                        }
                                        if (!start.equalsIgnoreCase("") && start != null) {
                                            sqlSearch += " OFFSET " + start + " ROWS FETCH NEXT " + limit + " ROWS ONLY ";
                                        }
                                        HashMap<String, Object> returnList_future = new HashMap<String, Object>();
                                        List<HashMap<String, Object>> serversList = new ArrayList<HashMap<String, Object>>();
                                        List<CoreGeografikesTheseisEntity> list= (List<CoreGeografikesTheseisEntity>) entityManager.createNativeQuery(sqlSearch, CoreGeografikesTheseisEntity.class).getResultList();

                                        for (CoreGeografikesTheseisEntity j : list) {
                                            HashMap<String, Object> sHmpam = new HashMap<String, Object>();
                                            sHmpam.put("code", j.getCode());
                                            sHmpam.put("title", j.getTitle());
                                            sHmpam.put("epipedo", j.getEpipedo());
                                            sHmpam.put("status", j.getStatus());
                                            sHmpam.put("active", j.getActive());
                                            sHmpam.put("id", j.getId());
                                            serversList.add(sHmpam);
                                        }
                                        returnList_future.put("data", serversList);
                                        returnList_future.put("total", listAll.size());
                                        returnList_future.put("status", "success");
                                        returnList_future.put("message", "ok");
                                        return returnList_future;
                                    });
                        },
                        executionContext);
                returnList = getFuture.get();
                DateFormat myDateFormat = new SimpleDateFormat("yyyy/MM/dd");
                ow.setDateFormat(myDateFormat);
                try {
                    jsonResult = ow.writeValueAsString(returnList);
                } catch (Exception e) {
                    e.printStackTrace();
                    result.put("status", "error");
                    result.put("message", "Πρόβλημα κατά την ανάγνωση των στοιχείων ");
                    return ok(result);
                }
                return ok(jsonResult);
            }
        }
    }

    //export methods
    @SuppressWarnings({"Duplicates", "unchecked"})
    public Result exportCoreGeografikesTheseisAsXLS(final Http.Request request) throws IOException {
        ObjectNode result = Json.newObject();
        try {
            JsonNode json = request.body().asJson();
            if (json == null) {
                return badRequest("Expecting Json data");
            } else {
                if (json == null) {
                    result.put("status", "error");
                    result.put("message", "Δεν εχετε αποστειλει εγκυρα δεδομενα.");
                    return ok(result);
                } else {
                    ObjectMapper ow = new ObjectMapper();
                    String jsonResult = "";
                    CompletableFuture<String> createXLSResult = CompletableFuture.supplyAsync(() -> {
                                return jpaApi.withTransaction(
                                        entityManager -> {
                                            String orderCol = json.findPath("orderCol").asText();
                                            String descAsc = json.findPath("descAsc").asText();
                                            String id = json.findPath("id").asText();
                                            String code = json.findPath("code").asText();
                                            String title = json.findPath("title").asText();
                                            String epipedo = json.findPath("epipedo").asText();
                                            ObjectNode resultNode = Json.newObject();
                                            String random_id = json.findPath("random_id").asText();
                                            String filename = "randomFile" + random_id + ".xls";
                                            HSSFWorkbook workbook = new HSSFWorkbook();
                                            HSSFSheet sheet = workbook.createSheet("FirstSheet");
                                            HSSFRow rowhead = sheet.createRow((short) 0);
                                            rowhead.createCell((short) 0).setCellValue("ID");
                                            rowhead.createCell((short) 1).setCellValue("ΚΩΔΙΚΟΣ");
                                            rowhead.createCell((short) 2).setCellValue("ΤΙΤΛΟΣ");
                                            rowhead.createCell((short) 3).setCellValue("ΕΠΙΠΕΔΟ");
                                            rowhead.createCell((short) 4).setCellValue("ΚΑΤΑΣΤΑΣΗ");
                                            rowhead.createCell((short) 5).setCellValue("ΕΝΕΡΓΟ");
                                            String sql = "select * from CORE_GEOGRAFIKES_THESEIS dte where 1=1 ";
                                            if (id != null && !id.equalsIgnoreCase("") && !id.equalsIgnoreCase("null") ) {
                                                sql += " and (dte.ID) like '%" + id + "%'";
                                            }
                                            if (code != null && !code.equalsIgnoreCase("") && !code.equalsIgnoreCase("null") ) {
                                                sql += " and (dte.CODE) like '%" + code + "%'";
                                            }
                                            if (title != null && !title.equalsIgnoreCase("") && !title.equalsIgnoreCase("null") ) {
                                                sql += " and (dte.TITLE) like '%" + title + "%'";
                                            }
                                            if (epipedo != null && !epipedo.equalsIgnoreCase("") && !epipedo.equalsIgnoreCase("null") ) {
                                                sql += " and (dte.EPIPEDO) like '%" + epipedo + "%'";
                                            }
                                            if (!orderCol.equalsIgnoreCase("") && orderCol != null) {
                                                sql += " order by " + orderCol + " " + descAsc;
                                            } else {
                                                sql += "order by id asc";
                                            }
                                            List<CoreGeografikesTheseisEntity> list = (List<CoreGeografikesTheseisEntity>)
                                                    entityManager.createNativeQuery(sql, CoreGeografikesTheseisEntity.class).getResultList();
                                            for (int i = 0; i < list.size(); i++) {
                                                HSSFRow row = sheet.createRow((short) i + 1);
                                                row.createCell((short) 0).setCellValue(list.get(i).getId());
                                                row.createCell((short) 1).setCellValue(list.get(i).getCode());
                                                row.createCell((short) 2).setCellValue(list.get(i).getTitle());
                                                row.createCell((short) 3).setCellValue(list.get(i).getEpipedo());

                                                if (list.get(i).getStatus().equals("DRAFT")) {
                                                    row.createCell((short) 4).setCellValue("ΠΡΟΧΕΙΡΟ");
                                                    row.createCell((short) 5).setCellValue("-");
                                                } else {
                                                    row.createCell((short) 4).setCellValue("ΕΠΙΚΥΡΩΜΕΝΟ");
                                                    if (list.get(i).getActive() == 1) {
                                                        row.createCell((short) 5).setCellValue("ΕΝΕΡΓΟ");
                                                    } else {
                                                        row.createCell((short) 5).setCellValue("ΑΝΕΝΕΡΓΟ");
                                                    }
                                                }
                                            }
                                            for (int col = 0; col < 13; col++) {
                                                sheet.autoSizeColumn(col);
                                            }
                                            FileOutputStream fileOut = null;
                                            try {
                                                fileOut = new FileOutputStream(filename);
                                                workbook.write(fileOut);
                                                fileOut.close();
                                                return filename;
                                            } catch (FileNotFoundException e) {
                                                e.printStackTrace();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            resultNode.put("message", "success");
                                            return filename;
                                        });
                            },
                            executionContext);
                    String ret_path = createXLSResult.get().toString();
                    File previewFile = new File(ret_path);
                    return ok(previewFile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", "error");
            result.put("message", "problem occured");
            return ok(result);
        }
    }
}
