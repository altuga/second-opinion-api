package org.jugistanbul.secondopinion.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import org.jugistanbul.secondopinion.api.config.BaseMockitoTest;
import org.jugistanbul.secondopinion.api.dto.PatientInformation;
import org.jugistanbul.secondopinion.api.dto.PatientResponse;
import org.jugistanbul.secondopinion.api.entity.Patient;
import org.jugistanbul.secondopinion.api.exception.EntityNotFoundException;
import org.jugistanbul.secondopinion.api.repository.PatientRepository;
import org.jugistanbul.secondopinion.api.service.converter.PatientEntityToInformationConverter;
import org.jugistanbul.secondopinion.api.service.converter.PatientRequestToEntityConverter;
import org.jugistanbul.secondopinion.api.service.validator.PatientValidator;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

public class PatientServiceTest extends BaseMockitoTest {

  @InjectMocks
  PatientService patientService;

  @Mock
  PatientValidator patientValidator;

  @Mock(answer = Answers.CALLS_REAL_METHODS)
  PatientRequestToEntityConverter patientConverter;

  @Mock
  PatientRepository patientRepository;

  @Mock
  PatientEntityToInformationConverter patientEntityToInformationConverter;

  @Test
  public void should_create_patient() throws Exception {
    //Given
    PatientInformation request = new PatientInformation();
    request.setUsername("eilhan");
    request.setPassword("test123");
    request.setEmail("eilhan@gmail.com");
    request.setPhone("05309547629");

    Patient patient = patientConverter.apply(request);

        //When
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

    //When
    PatientResponse patientResponse = patientService.create(request);

    //Then
    assertThat(patientResponse).isNotNull();
    assertThat(patientResponse.getPatientId()).isNotNull();
  }

  @Test
  public void should_throw_exception_when_request_is_missing() throws Exception {
    //Given
    PatientInformation request = null;

    doThrow(EntityNotFoundException.class).when(patientValidator).validate(request);

    //When
    Throwable throwable = catchThrowable(() -> patientService.create(request));

    //Then
    assertThat(throwable).isNotNull();
    assertThat(throwable).isInstanceOf(EntityNotFoundException.class);
  }

  @Test
  public void should_retrieve_patient() throws Exception {
    //Given
    Long id = 1L;

    //When
    PatientInformation patientInformation = patientService.retrievePatient(id);

    //Then
    InOrder inOrder = Mockito
        .inOrder(patientValidator, patientRepository, patientEntityToInformationConverter);
    inOrder.verify(patientValidator).validate(id);
    inOrder.verify(patientRepository).findOne(id);
    inOrder.verify(patientEntityToInformationConverter).apply(any(Patient.class));
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_throw_exception_when_patientId_is_null() throws Exception {
    //Given
    Long id = null;

    doThrow(EntityNotFoundException.class).when(patientValidator).validate(id);

    //When
    Throwable throwable = catchThrowable(() -> patientService.retrievePatient(id));

    //Then
    assertThat(throwable).isNotNull();
    assertThat(throwable).isInstanceOf(EntityNotFoundException.class);
  }
}
