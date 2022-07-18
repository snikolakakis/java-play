package controllers.PrjTde;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.execution_context.DatabaseExecutionContext;
import models.coreData.PrjTdeAttachmentsEntity;
import models.coreData.PrjTdeAttatchmentsDetailsEntity;
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
import java.util.Date;

import static play.mvc.Results.badRequest;
import static play.mvc.Results.ok;

public class PrjTdeAttachmentsController {

    private JPAApi jpaApi;
    private final WSClient ws;
    private DatabaseExecutionContext executionContext;

    @Inject
    public PrjTdeAttachmentsController(JPAApi jpaApi, DatabaseExecutionContext executionContext, WSClient ws) {
        this.jpaApi = jpaApi;
        this.executionContext = executionContext;
        this.ws = ws;
    }

    /**insert**/
    @SuppressWarnings({"Duplicates", "unchecked"})
    @BodyParser.Of(BodyParser.Json.class)
    public Result addPrjTdeAttachments(final Http.Request request) throws IOException {
        JsonNode json = request.body().asJson();
        boolean flagDetails;
        if (json == null) {
            return badRequest("Expecting Json data");
        } else {
            try {
                //JsonNode jsonArray = new ObjectMapper().readTree(Json.stringify(json)).get("tdeAttatchmentDetail");
                ObjectNode result = Json.newObject();
                CompletableFuture<JsonNode> addFuture = CompletableFuture.supplyAsync(() -> {
                            return jpaApi.withTransaction(entityManager -> {
                                ObjectNode add_result = Json.newObject();
                                Long user_id = json.findPath("user_id").asLong();
                                String title = json.findPath("title").asText();
                                String comments = json.findPath("comments").asText();
                                Long core_attachments_details_id = json.findPath("core_attachments_details_id").asLong();
                                Long prj_tde_id = json.findPath("prj_tde_id").asLong();
                                PrjTdeAttachmentsEntity entity = new PrjTdeAttachmentsEntity();
                                String sqlUnique = "select * from PRJ_TDE_ATTACHMENTS cms where cms.TITLE= '" + title + "'";
                                List<PrjTdeAttachmentsEntity> list = entityManager.createNativeQuery(sqlUnique, PrjTdeAttachmentsEntity.class).getResultList();
                                if (list.size() > 0) {
                                    add_result.put("status", "error");
                                    add_result.put("message", "Βρέθηκε εγγραφή με το ίδιο λεκτικό, προσπαθήστε ξανά");
                                    return add_result;
                                }
                                entity.setComments(comments);
                                entity.setTitle(title);
                                entity.setCore_attachments_details_id(core_attachments_details_id);
                                entity.setPrj_tde_id(prj_tde_id);
                                entityManager.persist(entity);
                                long do_id = entity.getId();
                                add_result.put("status", "success");
                                add_result.put("message", "Η καταχώρηση ολοκληρώθηκε με επιτυχία!");
                                add_result.put("user_id", user_id);
                                add_result.put("DO_ID", do_id);
                                add_result.put("system", "PRJ_TDE_ATTACHMENT");
                                add_result.put("system_label_front", "PrjTdeAttachments");
                                try{
                                    JsonNode jsonArray = new ObjectMapper().readTree(Json.stringify(json)).get("tdeAttatchmentDetail");
                                    int count = 0;
                                    for (JsonNode jsonNode : jsonArray) {
                                        count++;
                                        int category = jsonNode.findPath("category").asInt();
                                        if (category<0 || category>4){
                                            add_result.put("status"+ String.valueOf(count), "error");
                                            add_result.put("message"+ String.valueOf(count), "Το category δεν περιέχει σωστή τιμή");
                                            return add_result;

                                        }

                                        String label_el = jsonNode.findPath("label_el").asText();
                                        String label_en = jsonNode.findPath("label_en").asText();
                                        Date creation_date = new Date();
                                        PrjTdeAttatchmentsDetailsEntity entity2 = new PrjTdeAttatchmentsDetailsEntity();
                                        sqlUnique = "select * from PRJ_ATTATCHMENTS_DETAILS cms where cms.TDE_ATT_ID="+do_id +" and (cms.LABEL_EL= '" + label_el + "' or cms.LABEL_EN= '"+ label_en+"')";
                                        List<PrjTdeAttatchmentsDetailsEntity> list2 = entityManager.createNativeQuery(sqlUnique, PrjTdeAttatchmentsDetailsEntity.class).getResultList();
                                        if (list2.size() > 1) {
                                            add_result.put("status"+ String.valueOf(count), "error");
                                            add_result.put("message"+ String.valueOf(count), "Βρέθηκε εγγραφή με το label_el ή label_en που δώθηκε");
                                            return add_result;
                                        }

                                        entity2.setCategory(category);
                                        entity2.setTde_att_id(do_id);
                                        entity2.setLabel_el(label_el);
                                        entity2.setLabel_en(label_en);
                                        entity2.setCreation_date(creation_date);
                                        entityManager.persist(entity2);
                                    }
                                }catch(Exception e){
                                    e.printStackTrace();
                                    add_result.put("warning", "Δεν βρέθηκαν Details");
                                    return add_result;
                                }


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


    /**update**/
    @SuppressWarnings({"Duplicates", "unchecked"})
    @BodyParser.Of(BodyParser.Json.class)
    public Result updatePrjTdeAttachments(final Http.Request request) throws IOException {
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
                                String title = json.findPath("title").asText();
                                String comments = json.findPath("comments").asText();
                                Long core_attachments_details_id = json.findPath("core_attachments_details_id").asLong();
                                Long prj_tde_id = json.findPath("prj_tde_id").asLong();

                                PrjTdeAttachmentsEntity entity = entityManager.find(PrjTdeAttachmentsEntity.class, id);
                                if (entity == null) {
                                    update_result.put("status", "error");
                                    update_result.put("message", "Η εγγραφή που προσπαθείτε να ενημερώσετε δεν υπάρχει");
                                    return update_result;
                                }
                                String sqlUnique = "select * from PRJ_TDE_ATTACHMENTS cms where cms.TITLE = '" + title + "' and cms.ID != " + id;
                                List<PrjTdeAttachmentsEntity> list = entityManager.createNativeQuery(sqlUnique, PrjTdeAttachmentsEntity.class).getResultList();
                                if (list.size() > 0) {
                                    update_result.put("status", "error");
                                    update_result.put("message", "Βρέθηκε εγγραφή με το ίδιο λεκτικό,προσπαθήστε ξανά");
                                    return update_result;
                                }
                                entity.setComments(comments);
                                entity.setTitle(title);
                                entity.setPrj_tde_id(prj_tde_id);
                                entity.setCore_attachments_details_id(core_attachments_details_id);
                                entityManager.merge(entity);
                                update_result.put("status", "success");
                                update_result.put("message", "Η ενημέρωση ολοκληρώθηκε με επιτυχία!");
                                update_result.put("user_id", user_id);
                                update_result.put("DO_ID", entity.getId());
                                update_result.put("system", "PRJ_TDE_ATTACHMENT");
                                update_result.put("system_label_front", "PRJ TDE ATTACHMENT");
                                long do_id = entity.getId();

                                try{
                                    JsonNode jsonArray = new ObjectMapper().readTree(Json.stringify(json)).get("tdeAttatchmentDetail");
                                    sqlUnique = "delete from PRJ_ATTATCHMENTS_DETAILS cms where cms.TDE_ATT_ID = "+ do_id;
                                    entityManager.createNativeQuery(sqlUnique).executeUpdate();
                                    int count = 0;
                                    for (JsonNode jsonNode : jsonArray) {
                                        count++;
                                        int category = jsonNode.findPath("category").asInt();
                                        if (category<0 || category>4){
                                            update_result.put("status_details_"+ String.valueOf(count), "error");
                                            update_result.put("message_details"+ String.valueOf(count), "Το category δεν περιέχει σωστή τιμή");
                                            return update_result;

                                        }

                                        String label_el = jsonNode.findPath("label_el").asText();
                                        String label_en = jsonNode.findPath("label_en").asText();
                                        Date creation_date = new Date();
                                        PrjTdeAttatchmentsDetailsEntity entity2 = new PrjTdeAttatchmentsDetailsEntity();
                                        sqlUnique = "select * from PRJ_ATTATCHMENTS_DETAILS cms where cms.TDE_ATT_ID="+do_id +" and (cms.LABEL_EL= '" + label_el + "' or cms.LABEL_EN= '"+ label_en+"')";
                                        List<PrjTdeAttatchmentsDetailsEntity> list2 = entityManager.createNativeQuery(sqlUnique, PrjTdeAttatchmentsDetailsEntity.class).getResultList();
                                        if (list2.size() > 0) {
                                            update_result.put("status_details_"+ String.valueOf(count), "error");
                                            update_result.put("message_details_"+ String.valueOf(count), "Βρέθηκε εγγραφή με το label_el ή label_en που δώθηκε");
                                            return update_result;
                                        }

                                        entity2.setCategory(category);
                                        entity2.setTde_att_id(do_id);
                                        entity2.setLabel_el(label_el);
                                        entity2.setLabel_en(label_en);
                                        entity2.setCreation_date(creation_date);
                                        entityManager.persist(entity2);
                                    }
                                }catch(Exception e){
                                    e.printStackTrace();
                                    update_result.put("warning_details", "Δεν βρέθηκαν Details");
                                    return update_result;
                                }

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

    /**delete**/
    @BodyParser.Of(BodyParser.Json.class)
    public Result deletePrjTdeAttachments(final Http.Request request) throws IOException {
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
                                PrjTdeAttachmentsEntity entity = entityManager.find(PrjTdeAttachmentsEntity.class, id);
                                if (entity == null) {
                                    delete_result.put("status", "error");
                                    delete_result.put("message", "Η εγγραφή που προσπαθείτε να διαγράψετε δεν υπάρχει");
                                    return delete_result;
                                }
                                String sqlUnique = "delete from PRJ_ATTATCHMENTS_DETAILS cms where cms.TDE_ATT_ID = "+ entity.getId();
                                entityManager.createNativeQuery(sqlUnique).executeUpdate();
                                entityManager.remove(entity);
                                delete_result.put("status", "success");
                                delete_result.put("message", "Η διαγραφή ολοκληρώθηκε με επιτυχία!");
                                delete_result.put("user_id", user_id);
                                delete_result.put("DO_ID", entity.getId());
                                delete_result.put("system", "PRJ_TDE_ATTACHMENT");
                                delete_result.put("system_label_front", "Project Tde Attachment");
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
    public Result getPrjTdeAttachments(final Http.Request request) throws IOException, ExecutionException, InterruptedException {
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
                                        String comments = json.findPath("comments").asText();
                                        String title = json.findPath("title").asText();
                                        String core_attachments_details_id = json.findPath("core_attachments_details_id").asText();
                                        String prj_tde_id = json.findPath("prj_tde_id").asText();
                                        String start = json.findPath("start").asText();
                                        String limit = json.findPath("limit").asText();
                                        String sqlSearch = "select * from PRJ_TDE_ATTACHMENTS cms where 1=1 ";
                                        if (id != null && !id.equalsIgnoreCase("")) {
                                            sqlSearch += " and cms.ID like '%" + id + "%'";
                                        }
                                        if (comments != null && !comments.equalsIgnoreCase("")) {
                                            sqlSearch += " and cms.COMMENTS like '%" + comments + "%'";
                                        }
                                        if (title != null && !title.equalsIgnoreCase("")) {
                                            sqlSearch += " and cms.TITLE like '%" + title +"%'";
                                        }
                                        if (core_attachments_details_id != null && !core_attachments_details_id.equalsIgnoreCase("")) {
                                            sqlSearch += " and cms.CORE_ATTACHMENTS_DETAILS_ID like '%" + core_attachments_details_id + "%'";
                                        }
                                        if (prj_tde_id != null && !prj_tde_id.equalsIgnoreCase("")) {
                                            sqlSearch += " and cms.PRJ_TDE_ID like '%" + prj_tde_id + "%'";
                                        }
                                        List<PrjTdeAttachmentsEntity> listAll= (List<PrjTdeAttachmentsEntity>) entityManager.createNativeQuery(sqlSearch, PrjTdeAttachmentsEntity.class).getResultList();
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
                                        List<PrjTdeAttachmentsEntity> list= (List<PrjTdeAttachmentsEntity>) entityManager.createNativeQuery(sqlSearch, PrjTdeAttachmentsEntity.class).getResultList();
                                        for (PrjTdeAttachmentsEntity j : list) {
                                            HashMap<String, Object> sHmpam = new HashMap<String, Object>();

                                            String uniqueSql = "Select * from PRJ_ATTATCHMENTS_DETAILS cms where cms.TDE_ATT_ID="+j.getId();
                                            List<HashMap<String, Object>> serversListDet = new ArrayList<HashMap<String, Object>>();
                                            List<PrjTdeAttatchmentsDetailsEntity> list2= (List<PrjTdeAttatchmentsDetailsEntity>) entityManager.createNativeQuery(uniqueSql, PrjTdeAttatchmentsDetailsEntity.class).getResultList();
                                            for (PrjTdeAttatchmentsDetailsEntity i : list2) {
                                                HashMap<String, Object> sHmpam2 = new HashMap<String, Object>();
                                                sHmpam2.put("id", i.getId());
                                                sHmpam2.put("category", i.getCategory());
                                                sHmpam2.put("label_el", i.getLabel_el());
                                                sHmpam2.put("label_en", i.getLabel_en());
                                                sHmpam2.put("creation_date", i.getCreation_date());
                                                serversListDet.add(sHmpam2);
                                            }
                                            sHmpam.put("comments", j.getComments());
                                            sHmpam.put("title", j.getTitle());
                                            sHmpam.put("core_attachments_details_id", j.getCore_attachments_details_id());
                                            sHmpam.put("prj_tde_id", j.getPrj_tde_id());
                                            sHmpam.put("id", j.getId());
                                            sHmpam.put("PrjTdeAttachmentDetailsList ", serversListDet);
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





}
