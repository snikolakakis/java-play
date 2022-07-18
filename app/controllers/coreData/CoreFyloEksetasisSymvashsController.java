package controllers.coreData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.execution_context.DatabaseExecutionContext;
import models.coreData.CoreFyloEksetasisSymvashsEntity;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static play.mvc.Results.badRequest;
import static play.mvc.Results.ok;
public class CoreFyloEksetasisSymvashsController {
    private JPAApi jpaApi;
    private final WSClient ws;
    private DatabaseExecutionContext executionContext;

    @Inject
    public CoreFyloEksetasisSymvashsController(JPAApi jpaApi, DatabaseExecutionContext executionContext, WSClient ws) {
        this.jpaApi = jpaApi;
        this.executionContext = executionContext;
        this.ws=ws;
    }
    @SuppressWarnings({"Duplicates", "unchecked"})
    @BodyParser.Of(BodyParser.Json.class)
    public Result addCoreFyloEksetasisSymvashs(final Http.Request request) throws IOException {
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
                                String shmeio = json.findPath("shmeio").asText();
                                String category = json.findPath("category").asText();


                                CoreFyloEksetasisSymvashsEntity entity = new CoreFyloEksetasisSymvashsEntity();
                                String sqlUnique = "select * from CORE_FYLO_EKSETASHS_SYMVASHS cms where cms.CODE= '" + code + "'";
                                List<CoreFyloEksetasisSymvashsEntity> list = entityManager.createNativeQuery(sqlUnique, CoreFyloEksetasisSymvashsEntity.class).getResultList();
                                if (list.size() > 0) {
                                    add_result.put("status", "error");
                                    add_result.put("message", "Βρέθηκε εγγραφή με το ίδιο λεκτικό, προσπαθήστε ξανά");
                                    return add_result;
                                }
                                entity.setCode(code);
                                entity.setShmeio(shmeio);
                                entity.setCategory(category);
                                entity.setStatus("DRAFT");
                                entity.setActive(1);
                                entity.setVerified_user(0);
                                entityManager.persist(entity);
                                add_result.put("status", "success");
                                add_result.put("message", "Η καταχώρηση ολοκληρώθηκε με επιτυχία!");
                                add_result.put("user_id", user_id);
                                add_result.put("DO_ID", entity.getId());
                                add_result.put("system", "CORE_FYLO_EKSETASIS_SYMVASHS");
                                add_result.put("system_label_front", "ΦΥΛΟ ΕΞΕΤΑΣΗΣ ΣΥΜΒΑΣΗΣ");
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
    public Result updateCoreFyloEksetasisSymvashs(final Http.Request request) throws IOException {
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
                                Integer active= json.findPath("active").asInt();
                                String code= json.findPath("code").asText();
                                String shmeio= json.findPath("shmeio").asText();
                                String status= json.findPath("status").asText();
                                String category= json.findPath("category").asText();

                                CoreFyloEksetasisSymvashsEntity entity = entityManager.find(CoreFyloEksetasisSymvashsEntity.class, id);
                                if (entity == null) {
                                    update_result.put("status", "error");
                                    update_result.put("message", "Η εγγραφή που προσπαθείτε να ενημερώσετε δεν υπάρχει");
                                    return update_result;
                                }
                                String sqlUnique = "select * from CORE_FYLO_EKSETASHS_SYMVASHS cms where cms.CODE = '" + code + "' and ID != " + id;
                                List<CoreFyloEksetasisSymvashsEntity> list = entityManager.createNativeQuery(sqlUnique, CoreFyloEksetasisSymvashsEntity.class).getResultList();
                                if (list.size() > 0) {
                                    update_result.put("status", "error");
                                    update_result.put("message", "Βρέθηκε εγγραφή με το ίδιο λεκτικό,προσπαθήστε ξανά");
                                    return update_result;
                                }
                                entity.setCode(code);
                                entity.setShmeio(shmeio);
                                entity.setCategory(category);
                                entity.setStatus(status);
                                entity.setActive(active);
                                entityManager.merge(entity);
                                update_result.put("status", "success");
                                update_result.put("message", "Η ενημέρωση ολοκληρώθηκε με επιτυχία!");
                                update_result.put("user_id", user_id);
                                update_result.put("DO_ID", entity.getId());
                                update_result.put("system", "CORE_FYLO_EKSETASHS_SYMVASHS");
                                update_result.put("system_label_front", "ΦΥΛΟ ΕΞΕΤΑΣΗΣ ΣΥΜΒΑΣΗΣ");
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
    public Result deleteCoreFyloEksetasisSymvashs(final Http.Request request) throws IOException {
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
                                CoreFyloEksetasisSymvashsEntity entity = entityManager.find(CoreFyloEksetasisSymvashsEntity.class, id);
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
                                delete_result.put("system", "CORE_FYLO_EKSETASHS_SYMVASHS");
                                delete_result.put("system_label_front", "ΦΥΛΟ ΕΞΕΤΑΣΗΣ ΣΥΜΒΑΣΗΣ");
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
    public Result getCoreFyloEksetasisSymvashs(final Http.Request request) throws IOException, ExecutionException, InterruptedException {
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
                                        String verification_date = json.findPath("verification_date").asText();
                                        String code = json.findPath("code").asText();
                                        String shmeio = json.findPath("shmeio").asText();
                                        String status = json.findPath("status").asText();
                                        String category = json.findPath("category").asText();
                                        String start = json.findPath("start").asText();
                                        String limit = json.findPath("limit").asText();

                                        String sqlSearch = "select * from CORE_FYLO_EKSETASHS_SYMVASHS cms where 1=1 ";
                                        if (id != null && !id.equalsIgnoreCase("")) {
                                            sqlSearch += " and cms.ID like '%" + id + "%'";
                                        }
                                        if (verification_date != null && !verification_date.equalsIgnoreCase("")) {
                                            sqlSearch += " and cms.VERIFICATION_DATE like '%" + verification_date + "%'";
                                        }
                                        if (shmeio != null && !shmeio.equalsIgnoreCase("")) {
                                            sqlSearch += " and cms.SHMEIO like '%" + shmeio + "%'";
                                        }
                                        if (code != null && !code.equalsIgnoreCase("")) {
                                            sqlSearch += " and cms.CODE like '%" + code + "%'";
                                        }
                                        if (status != null && !status.equalsIgnoreCase("")) {
                                            sqlSearch += " and cms.STATUS like '%" + status +"%'";
                                        }
                                        if (category != null && !category.equalsIgnoreCase("")) {
                                            sqlSearch += " and cms.CATEGORY like '%" + category + "%'";
                                        }
                                        List<CoreFyloEksetasisSymvashsEntity> listAll= (List<CoreFyloEksetasisSymvashsEntity>) entityManager.createNativeQuery(sqlSearch, CoreFyloEksetasisSymvashsEntity.class).getResultList();

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
                                        List<CoreFyloEksetasisSymvashsEntity> list= (List<CoreFyloEksetasisSymvashsEntity>) entityManager.createNativeQuery(sqlSearch, CoreFyloEksetasisSymvashsEntity.class).getResultList();

                                        for (CoreFyloEksetasisSymvashsEntity j : list) {
                                            HashMap<String, Object> sHmpam = new HashMap<String, Object>();
                                            sHmpam.put("code", j.getCode());
                                            sHmpam.put("verification_date", j.getVerification_date());
                                            sHmpam.put("verified_user", j.getVerified_user());
                                            sHmpam.put("status", j.getStatus());
                                            sHmpam.put("shmeio", j.getShmeio());
                                            sHmpam.put("category", j.getCategory());
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

    @BodyParser.Of(BodyParser.Json.class)
    public Result verifiedRecordCoreFyloEksetasisSymvashs(final Http.Request request) throws IOException {
        JsonNode json = request.body().asJson();
        if (json == null) {
            return badRequest("Expecting Json data");
        } else {
            try {
                ObjectNode result = Json.newObject();
                CompletableFuture<JsonNode> verifiedFuture = CompletableFuture.supplyAsync(() -> {
                            return jpaApi.withTransaction(entityManager -> {
                                ObjectNode verification_result = Json.newObject();
                                Long id = json.findPath("id").asLong();
                                Long user_id = json.findPath("user_id").asLong();
                                CoreFyloEksetasisSymvashsEntity entity = entityManager.find(CoreFyloEksetasisSymvashsEntity.class, id);
                                if (entity == null) {
                                    verification_result.put("status", "error");
                                    verification_result.put("message", "Η εγγραφή που προσπαθείτε να επικυρώσετε δεν υπάρχει");
                                    return verification_result;
                                }
                                entity.setVerified_user(1);
                                entity.setActive(1);
                                entity.setStatus("VERIFIED");
                                entity.setVerification_date(new Date());
                                entityManager.merge(entity);
                                verification_result.put("status", "success");
                                verification_result.put("message", "Η επικύρωση ολοκληρώθηκε με επιτυχία!");
                                verification_result.put("user_id", user_id);
                                verification_result.put("DO_ID", entity.getId());
                                verification_result.put("system", "CORE_FYLO_EKSETASHS_SYMVASHS");
                                verification_result.put("system_label_front", "ΦΥΛΟ ΕΞΕΤΑΣΗΣ ΣΥΜΒΑΣΗΣ");
                                return verification_result;
                            });
                        },
                        executionContext);
                result = (ObjectNode) verifiedFuture.get();
                return ok(result);
            } catch (Exception e) {
                ObjectNode result = Json.newObject();
                e.printStackTrace();
                result.put("status", "error");
                result.put("message", "Προβλημα κατα την επικύρωση");
                return ok(result);
            }
        }

    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result activeInactiveRecordCoreFyloEksetasisSymvashs(final Http.Request request) throws IOException {
        JsonNode json = request.body().asJson();
        if (json == null) {
            return badRequest("Expecting Json data");
        } else {
            try {
                ObjectNode result = Json.newObject();
                CompletableFuture<JsonNode> activeInactiveFuture = CompletableFuture.supplyAsync(() -> {
                            return jpaApi.withTransaction(entityManager -> {
                                ObjectNode activeInactive_result = Json.newObject();
                                Long id = json.findPath("id").asLong();
                                Long user_id = json.findPath("user_id").asLong();
                                String msg;
                                CoreFyloEksetasisSymvashsEntity entity = entityManager.find(CoreFyloEksetasisSymvashsEntity.class, id);
                                if (entity == null) {
                                    activeInactive_result.put("status", "error");
                                    activeInactive_result.put("message", "Η εγγραφή που προσπαθείτε να ενεργοποιήσετε/απενεργοποιήσετε δεν υπάρχει");
                                    return activeInactive_result;
                                }
                                if (entity.getActive()==0){
                                    entity.setActive(1);
                                    msg = "Η ενεργοποίηση ολοκληρώθηκε με επιτυχία!";
                                }else{
                                    entity.setActive(0);
                                    msg = "Η απενεργοποίηση ολοκληρώθηκε με επιτυχία!";
                                }

                                entityManager.merge(entity);
                                activeInactive_result.put("status", "success");
                                activeInactive_result.put("message", msg);
                                activeInactive_result.put("user_id", user_id);
                                activeInactive_result.put("DO_ID", entity.getId());
                                activeInactive_result.put("system", "CORE_FYLO_EKSETASHS_SYMVASHS");
                                activeInactive_result.put("system_label_front", "ΦΥΛΟ ΕΞΕΤΑΣΗΣ ΣΥΜΒΑΣΗΣ");
                                return activeInactive_result;
                            });
                        },
                        executionContext);
                result = (ObjectNode) activeInactiveFuture.get();
                return ok(result);
            } catch (Exception e) {
                ObjectNode result = Json.newObject();
                e.printStackTrace();
                result.put("status", "error");
                result.put("message", "Προβλημα κατα την ενεργοποίηση/απενεργοποίηση");
                return ok(result);
            }
        }

    }

}
