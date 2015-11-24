package org.clockin.web.rest;

import org.clockin.Application;
import org.clockin.domain.Clockin;
import org.clockin.repository.ClockinRepository;
import org.clockin.repository.search.ClockinSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.clockin.domain.enumeration.RegistryType;

/**
 * Test class for the ClockinResource REST controller.
 *
 * @see ClockinResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class ClockinResourceTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.of("Z"));

    private static final String DEFAULT_SEQUENTIAL_REGISTER_NUMBER = "AAAAA";
    private static final String UPDATED_SEQUENTIAL_REGISTER_NUMBER = "BBBBB";

    private static final ZonedDateTime DEFAULT_DATE_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_DATE_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_DATE_TIME_STR = dateTimeFormatter.format(DEFAULT_DATE_TIME);


private static final RegistryType DEFAULT_REGISTRY_TYPE = RegistryType.TYPE_1;
    private static final RegistryType UPDATED_REGISTRY_TYPE = RegistryType.TYPE_2;

    @Inject
    private ClockinRepository clockinRepository;

    @Inject
    private ClockinSearchRepository clockinSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restClockinMockMvc;

    private Clockin clockin;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ClockinResource clockinResource = new ClockinResource();
        ReflectionTestUtils.setField(clockinResource, "clockinRepository", clockinRepository);
        ReflectionTestUtils.setField(clockinResource, "clockinSearchRepository", clockinSearchRepository);
        this.restClockinMockMvc = MockMvcBuilders.standaloneSetup(clockinResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        clockin = new Clockin();
        clockin.setSequentialRegisterNumber(DEFAULT_SEQUENTIAL_REGISTER_NUMBER);
        clockin.setDateTime(DEFAULT_DATE_TIME);
        clockin.setRegistryType(DEFAULT_REGISTRY_TYPE);
    }

    @Test
    @Transactional
    public void createClockin() throws Exception {
        int databaseSizeBeforeCreate = clockinRepository.findAll().size();

        // Create the Clockin

        restClockinMockMvc.perform(post("/api/clockins")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(clockin)))
                .andExpect(status().isCreated());

        // Validate the Clockin in the database
        List<Clockin> clockins = clockinRepository.findAll();
        assertThat(clockins).hasSize(databaseSizeBeforeCreate + 1);
        Clockin testClockin = clockins.get(clockins.size() - 1);
        assertThat(testClockin.getSequentialRegisterNumber()).isEqualTo(DEFAULT_SEQUENTIAL_REGISTER_NUMBER);
        assertThat(testClockin.getDateTime()).isEqualTo(DEFAULT_DATE_TIME);
        assertThat(testClockin.getRegistryType()).isEqualTo(DEFAULT_REGISTRY_TYPE);
    }

    @Test
    @Transactional
    public void getAllClockins() throws Exception {
        // Initialize the database
        clockinRepository.saveAndFlush(clockin);

        // Get all the clockins
        restClockinMockMvc.perform(get("/api/clockins"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(clockin.getId().intValue())))
                .andExpect(jsonPath("$.[*].sequentialRegisterNumber").value(hasItem(DEFAULT_SEQUENTIAL_REGISTER_NUMBER.toString())))
                .andExpect(jsonPath("$.[*].dateTime").value(hasItem(DEFAULT_DATE_TIME_STR)))
                .andExpect(jsonPath("$.[*].registryType").value(hasItem(DEFAULT_REGISTRY_TYPE.toString())));
    }

    @Test
    @Transactional
    public void getClockin() throws Exception {
        // Initialize the database
        clockinRepository.saveAndFlush(clockin);

        // Get the clockin
        restClockinMockMvc.perform(get("/api/clockins/{id}", clockin.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(clockin.getId().intValue()))
            .andExpect(jsonPath("$.sequentialRegisterNumber").value(DEFAULT_SEQUENTIAL_REGISTER_NUMBER.toString()))
            .andExpect(jsonPath("$.dateTime").value(DEFAULT_DATE_TIME_STR))
            .andExpect(jsonPath("$.registryType").value(DEFAULT_REGISTRY_TYPE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingClockin() throws Exception {
        // Get the clockin
        restClockinMockMvc.perform(get("/api/clockins/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateClockin() throws Exception {
        // Initialize the database
        clockinRepository.saveAndFlush(clockin);

		int databaseSizeBeforeUpdate = clockinRepository.findAll().size();

        // Update the clockin
        clockin.setSequentialRegisterNumber(UPDATED_SEQUENTIAL_REGISTER_NUMBER);
        clockin.setDateTime(UPDATED_DATE_TIME);
        clockin.setRegistryType(UPDATED_REGISTRY_TYPE);

        restClockinMockMvc.perform(put("/api/clockins")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(clockin)))
                .andExpect(status().isOk());

        // Validate the Clockin in the database
        List<Clockin> clockins = clockinRepository.findAll();
        assertThat(clockins).hasSize(databaseSizeBeforeUpdate);
        Clockin testClockin = clockins.get(clockins.size() - 1);
        assertThat(testClockin.getSequentialRegisterNumber()).isEqualTo(UPDATED_SEQUENTIAL_REGISTER_NUMBER);
        assertThat(testClockin.getDateTime()).isEqualTo(UPDATED_DATE_TIME);
        assertThat(testClockin.getRegistryType()).isEqualTo(UPDATED_REGISTRY_TYPE);
    }

    @Test
    @Transactional
    public void deleteClockin() throws Exception {
        // Initialize the database
        clockinRepository.saveAndFlush(clockin);

		int databaseSizeBeforeDelete = clockinRepository.findAll().size();

        // Get the clockin
        restClockinMockMvc.perform(delete("/api/clockins/{id}", clockin.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Clockin> clockins = clockinRepository.findAll();
        assertThat(clockins).hasSize(databaseSizeBeforeDelete - 1);
    }
}
