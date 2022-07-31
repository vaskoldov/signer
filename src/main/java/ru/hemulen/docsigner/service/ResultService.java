package ru.hemulen.docsigner.service;

import org.springframework.stereotype.Service;
import ru.hemulen.docsigner.entity.DBConnection;
import ru.hemulen.docsigner.exception.ResponseParseException;
import ru.hemulen.docsigner.model.MessageResponse;
import ru.hemulen.docsigner.model.ResultResponse;

import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class ResultService {

    private DBConnection connection;

    ResultService() {
        this.connection = new DBConnection();
    }

    public ResultResponse getResult(String clientId) throws ResponseParseException {
        ResultResponse resultResponse = new ResultResponse();
        resultResponse.setResult("IN_PROCESS");
        // Получаем все ответы на запрос с clientId
        ResultSet resultSet = this.connection.getAnswers(clientId);
        try {
            while (resultSet.next()) {
                MessageResponse messageResponse = new MessageResponse();
                messageResponse.setType(resultSet.getString("mode"));
                switch (messageResponse.getType()) {
                    case "STATUS":
                        // Уже установлен результат "В процессе". Статус на него не влияет.
                        break;
                    case "REJECT":
                    case "ERROR":
                    case "MESSAGE":
                        resultResponse.setResult("COMPLETED");
                        break;
                }
            }
        } catch (SQLException e) {
            throw new ResponseParseException("Не удалось сформировать ответ");
        }
        return resultResponse;
    }
}
