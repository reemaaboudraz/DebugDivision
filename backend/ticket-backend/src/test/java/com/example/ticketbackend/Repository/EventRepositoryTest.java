package com.example.ticketbackend.Repository;

import com.example.ticketbackend.Model.Event;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventRepositoryTest {

    @Mock
    private Firestore firestore;

    @Mock
    private CollectionReference collectionReference;

    @Mock
    private DocumentReference documentReference;

    @Mock
    private DocumentSnapshot documentSnapshot;

    @Mock
    private QueryDocumentSnapshot queryDocumentSnapshot;

    @Mock
    private QuerySnapshot querySnapshot;

    @Mock
    private ApiFuture<DocumentReference> documentReferenceFuture;

    @Mock
    private ApiFuture<DocumentSnapshot> documentSnapshotFuture;

    @Mock
    private ApiFuture<QuerySnapshot> querySnapshotFuture;

    @Mock
    private ApiFuture<WriteResult> writeResultFuture;

    @InjectMocks
    private EventRepository eventRepository;

    private Event testEvent;

    @BeforeEach
    void setUp() {
        testEvent = new Event();
        testEvent.setId("test-event-id");
        testEvent.setName("Test Event");
        testEvent.setLocation("Campus Hall");
        testEvent.setAvailableTickets(50);
        testEvent.setEventDate(Timestamp.ofTimeSecondsAndNanos(1893456000L, 0));
        testEvent.setOrganizerId("organizer-123");
    }

    /**
     * saveEvent tests
     */

    @Test
    void testSaveEvent_Success() throws ExecutionException, InterruptedException {
        when(firestore.collection("events")).thenReturn(collectionReference);
        when(collectionReference.add(testEvent)).thenReturn(documentReferenceFuture);
        when(documentReferenceFuture.get()).thenReturn(documentReference);
        when(documentReference.getId()).thenReturn("generated-id");

        String result = eventRepository.saveEvent(testEvent);

        assertEquals("generated-id", result);
        assertEquals("generated-id", testEvent.getId()); // id should be set back on object
        verify(firestore).collection("events");
        verify(collectionReference).add(testEvent);
        verify(documentReferenceFuture).get();
        verify(documentReference).getId();
    }

    @Test
    void testSaveEvent_ExecutionException() throws ExecutionException, InterruptedException {
        when(firestore.collection("events")).thenReturn(collectionReference);
        when(collectionReference.add(testEvent)).thenReturn(documentReferenceFuture);
        when(documentReferenceFuture.get()).thenThrow(new ExecutionException("Database error", new Throwable()));

        assertThrows(ExecutionException.class, () -> {
            eventRepository.saveEvent(testEvent);
        });

        verify(firestore).collection("events");
        verify(collectionReference).add(testEvent);
    }

    /**
     * getEventById tests
     */

    @Test
    void testGetEventById_Success() throws ExecutionException, InterruptedException {
        when(firestore.collection("events")).thenReturn(collectionReference);
        when(collectionReference.document("test-event-id")).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.toObject(Event.class)).thenReturn(testEvent);
        when(documentSnapshot.getId()).thenReturn("test-event-id");

        // Act
        Event result = eventRepository.getEventById("test-event-id");

        // Assert
        assertNotNull(result);
        assertEquals("test-event-id", result.getId());
        assertEquals("Test Event", result.getName());
        assertEquals("Campus Hall", result.getLocation());
        assertEquals(50, result.getAvailableTickets());

        verify(firestore).collection("events");
        verify(collectionReference).document("test-event-id");
        verify(documentReference).get();
        verify(documentSnapshot).exists();
        verify(documentSnapshot).toObject(Event.class);
    }

    @Test
    void testGetEventById_NotFound() throws ExecutionException, InterruptedException {
        when(firestore.collection("events")).thenReturn(collectionReference);
        when(collectionReference.document("nonexistent-id")).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(false);

        // Act
        Event result = eventRepository.getEventById("nonexistent-id");

        // Assert
        assertNull(result);

        verify(firestore).collection("events");
        verify(collectionReference).document("nonexistent-id");
        verify(documentReference).get();
        verify(documentSnapshot).exists();
        verify(documentSnapshot, never()).toObject(Event.class);
    }

    @Test
    void testGetEventById_ExecutionException() throws ExecutionException, InterruptedException {
        when(firestore.collection("events")).thenReturn(collectionReference);
        when(collectionReference.document("test-event-id")).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenThrow(new ExecutionException("Database error", new Throwable()));

        assertThrows(ExecutionException.class, () -> {
            eventRepository.getEventById("test-event-id");
        });

        verify(firestore).collection("events");
        verify(collectionReference).document("test-event-id");
        verify(documentReference).get();
    }

    /**
     * getAllEvents tests
     */

    @Test
    void testGetAllEvents_Success() throws ExecutionException, InterruptedException {
        Event secondEvent = new Event();
        secondEvent.setId("test-event-id-2");
        secondEvent.setName("Second Event");
        secondEvent.setLocation("Somewhere");
        secondEvent.setAvailableTickets(100);

        QueryDocumentSnapshot queryDoc2 = mock(QueryDocumentSnapshot.class);

        when(firestore.collection("events")).thenReturn(collectionReference);
        when(collectionReference.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Arrays.asList(queryDocumentSnapshot, queryDoc2));

        when(queryDocumentSnapshot.toObject(Event.class)).thenReturn(testEvent);
        when(queryDocumentSnapshot.getId()).thenReturn("test-event-id");

        when(queryDoc2.toObject(Event.class)).thenReturn(secondEvent);
        when(queryDoc2.getId()).thenReturn("test-event-id-2");

        // Act
        List<Event> result = eventRepository.getAllEvents();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("test-event-id", result.get(0).getId());
        assertEquals("Test Event", result.get(0).getName());
        assertEquals("test-event-id-2", result.get(1).getId());
        assertEquals("Second Event", result.get(1).getName());

        verify(firestore).collection("events");
        verify(collectionReference).get();
        verify(querySnapshot).getDocuments();
    }

    @Test
    void testGetAllEvents_Empty() throws ExecutionException, InterruptedException {
        when(firestore.collection("events")).thenReturn(collectionReference);
        when(collectionReference.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Collections.emptyList());

        // Act
        List<Event> result = eventRepository.getAllEvents();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());

        verify(firestore).collection("events");
        verify(collectionReference).get();
        verify(querySnapshot).getDocuments();
    }

    @Test
    void testGetAllEvents_ExecutionException() throws ExecutionException, InterruptedException {
        when(firestore.collection("events")).thenReturn(collectionReference);
        when(collectionReference.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenThrow(new ExecutionException("Database error", new Throwable()));

        assertThrows(ExecutionException.class, () -> {
            eventRepository.getAllEvents();
        });

        verify(firestore).collection("events");
        verify(collectionReference).get();
    }

    /**
     * updateEvent tests
     */

    @Test
    void testUpdateEvent_Success() throws ExecutionException, InterruptedException {
        when(firestore.collection("events")).thenReturn(collectionReference);
        when(collectionReference.document("test-event-id")).thenReturn(documentReference);
        when(documentReference.set(testEvent)).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(null);

        // Act
        eventRepository.updateEvent(testEvent);

        // Assert
        verify(firestore).collection("events");
        verify(collectionReference).document("test-event-id");
        verify(documentReference).set(testEvent);
        verify(writeResultFuture).get();
    }

    @Test
    void testUpdateEvent_ExecutionException() throws ExecutionException, InterruptedException {
        when(firestore.collection("events")).thenReturn(collectionReference);
        when(collectionReference.document("test-event-id")).thenReturn(documentReference);
        when(documentReference.set(testEvent)).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenThrow(new ExecutionException("Database error", new Throwable()));

        assertThrows(ExecutionException.class, () -> {
            eventRepository.updateEvent(testEvent);
        });

        verify(firestore).collection("events");
        verify(collectionReference).document("test-event-id");
        verify(documentReference).set(testEvent);
    }

    /**
     * deleteEvent tests
     */

    @Test
    void testDeleteEvent_Success() throws ExecutionException, InterruptedException {
        when(firestore.collection("events")).thenReturn(collectionReference);
        when(collectionReference.document("test-event-id")).thenReturn(documentReference);
        when(documentReference.delete()).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(null);

        // Act
        eventRepository.deleteEvent("test-event-id");

        // Assert
        verify(firestore).collection("events");
        verify(collectionReference).document("test-event-id");
        verify(documentReference).delete();
        verify(writeResultFuture).get();
    }

    @Test
    void testDeleteEvent_ExecutionException() throws ExecutionException, InterruptedException {
        when(firestore.collection("events")).thenReturn(collectionReference);
        when(collectionReference.document("test-event-id")).thenReturn(documentReference);
        when(documentReference.delete()).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenThrow(new ExecutionException("Database error", new Throwable()));

        assertThrows(ExecutionException.class, () -> {
            eventRepository.deleteEvent("test-event-id");
        });

        verify(firestore).collection("events");
        verify(collectionReference).document("test-event-id");
        verify(documentReference).delete();
    }
}