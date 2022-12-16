package io.busservice.ticketprice;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.busservice.ticketprice.dto.Passenger;
import io.busservice.ticketprice.dto.PassengerType;
import io.busservice.ticketprice.dto.PriceRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest
public class TicketControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    TicketService service;

    @Test
    public void shouldCallApiCorrectly() throws Exception {
        String[] requests =new String[3];

        requests[0] = "{\"from\":\"Riga\",\"to\":\"Vilnius\",\"passengers\":[{\"passengerType\":\"ADULT\",\"luggageCount\":1}]}";
        requests[1] = "{\"from\":\"Riga\",\"to\":\"Vilnius\",\"passengers\":[{\"passengerType\":\"chILD\",\"luggageCount\":0}]}";
        requests[2] = "{\"from\":\"Riga\",\"to\":\"Vilnius\",\"passengers\":[{\"passengerType\":\"pensioner\",\"luggageCount\":2}]}";

        for (String req : requests) {
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/ticket-prices")
                    .content(req)
                    .contentType(MediaType.APPLICATION_JSON);

            mvc.perform(requestBuilder)
                    .andExpect(status().is(200));
        }
    }

    @Test
    public void shouldFailOnIncorrectRequestsStartEnd() throws Exception {
        ObjectMapper jsonObjectMapper = new ObjectMapper();

        Passenger passengerAdult = new Passenger(PassengerType.ADULT, 2);
        Passenger[] passengers = new Passenger[] { passengerAdult };
        PriceRequest priceRequest1 = new PriceRequest("", "Vilnius", passengers);
        PriceRequest priceRequest2 = new PriceRequest("Riga", "", passengers);
        PriceRequest priceRequest3 = new PriceRequest(null, "", passengers);
        PriceRequest priceRequest4 = new PriceRequest("Riga", null, passengers);

        PriceRequest[] priceRequests = {priceRequest1, priceRequest2, priceRequest3, priceRequest4};

        for (PriceRequest req : priceRequests) {
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/ticket-prices")
                    .content(jsonObjectMapper.writeValueAsString(req))
                    .contentType(MediaType.APPLICATION_JSON);

            mvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(content().string(""));
        }
    }

    @Test
    public void shouldFailOnIncorrectPassengerTypes() throws Exception {
        String[] requests =new String[3];

        requests[0] = "{\"from\":\"Riga\",\"to\":\"Vilnius\",\"passengers\":[{\"passengerType\":\"\",\"luggageCount\":1}]}";
        requests[1] = "{\"from\":\"Riga\",\"to\":\"Vilnius\",\"passengers\":[{\"passengerType\":\"other\",\"luggageCount\":1}]}";
        requests[2] = "{\"from\":\"Riga\",\"to\":\"Vilnius\",\"passengers\":[{\"passengerType\":null,\"luggageCount\":1}]}";

        for (String req : requests) {
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/ticket-prices")
                    .content(req)
                    .contentType(MediaType.APPLICATION_JSON);

            mvc.perform(requestBuilder)
                    .andExpect(status().is(400));
        }
    }

}

