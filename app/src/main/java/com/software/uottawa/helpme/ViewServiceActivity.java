package com.software.uottawa.helpme;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**Classe pratiquement identique à EditTaskAcitivty, mais les items ne sont pas selectionnables
 *
 *
 */
public class ViewServiceActivity extends AppCompatActivity {

    private static final String EXTRA_SERVICE_ID = "com.majes.uottawa.taskmanager.service_id";
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseServices;
    private DatabaseReference mDatabaseNotes;
    private DatabaseReference mDatabaseRes;
    private FirebaseAuth mAuth;

    private ListView listViewInst;
    private ListView listViewFeed;
    private List<Note> inst;
    private List<Note> feed;


    private Service mService;
    private User mUser;

    private TextView mServiceName;
    private TextView mServiceDescription;
    private TextView mServiceInstruction;
    private TextView mServiceHourlyRate;

    private RadioGroup mRadioGroup;
    private RadioGroup mServiceRes;


    private String extraServiceId;

    public static Intent newIntent(Context packageContext, String serviceId) {
        Intent intent = new Intent(packageContext, ViewServiceActivity.class);
        intent.putExtra(EXTRA_SERVICE_ID, serviceId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_service);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        extraServiceId = getIntent().getStringExtra(EXTRA_SERVICE_ID);

        mDatabaseServices = FirebaseDatabase.getInstance().getReference("services");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference("users");
        mDatabaseNotes = FirebaseDatabase.getInstance().getReference("Notes");

        listViewFeed = findViewById(R.id.feedback);
        listViewInst = findViewById(R.id.instruction);

        inst = new ArrayList<>();
        feed = new ArrayList<>();



        listViewInst.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Note note = inst.get(position);
                update(note);
                return true;
            }
        });

        listViewFeed.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Note note = feed.get(position);
                update(note);
                return true;
            }
        });

        mAuth = FirebaseAuth.getInstance();


        mServiceName = findViewById(R.id.edit_service_name);
        mServiceDescription = findViewById(R.id.edit_service_description);
        mServiceInstruction = findViewById(R.id.edit_service_instruction);
        mServiceHourlyRate = findViewById(R.id.edit_due_hourly_rate);
        mRadioGroup = findViewById(R.id.edit_status_radio_group);
        mServiceRes = findViewById(R.id.edit_res_radio_group);




        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        mServiceName.addTextChangedListener(textWatcher);
        mServiceDescription.addTextChangedListener(textWatcher);
        mServiceInstruction.addTextChangedListener(textWatcher);
        mServiceHourlyRate.addTextChangedListener(textWatcher);
    }

    private void setSupportActionBar(Toolbar toolbar) {
    }


    @Override
    protected void onStart() {
        super.onStart();

        mDatabaseServices.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Service service = snapshot.getValue(Service.class);
                    if (service.getId().equals(extraServiceId)) {
                        mService = service;
                    }
                }
                serviceStatus();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user.getId().equals(mAuth.getCurrentUser().getUid())) {
                        mUser = user;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mDatabaseNotes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                inst.clear();
                feed.clear();

                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    Note note = snap.getValue(Note.class);
                    //inst.add(note);

                    if(note.getTacheId().equals(mService.getId())) {               // if note.tachedID != tacheID, on n'ajoute pas ces taches a feed ou inst
                        if (note.getCreator().equals(mUser.getId())) {             // note a ete ecrit par le createur de la tache
                            inst.add(note);                                        // add to inst
                        } else {
                            feed.add(note);                                 // note n'a pas ete ecrit par le createur de la tache
                        }
                    }
                }

                NoteAdapter noteAdapter = new NoteAdapter(ViewServiceActivity.this, inst);         //Create adapter pour lse listviews
                listViewInst.setAdapter(noteAdapter);
                NoteAdapter feedAdapter = new NoteAdapter(ViewServiceActivity.this, feed);// Display les listview
                listViewFeed.setAdapter(feedAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    private void serviceStatus() {
        mServiceName.setText(mService.getTitle());
        mServiceDescription.setText(mService.getDescription());
        mServiceInstruction.setText(mService.getInstruction());
        mServiceHourlyRate.setText(mService.getRate());

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.note_settings) {
            addNote();

        }
        return true;
    }












    private void update(final Note note) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_note_update, null);
        dialogBuilder.setView(dialogView);

        EditText noteEvent = findViewById(R.id.noteComment);
        noteEvent.setText("Edit Note");
        final EditText editNote = dialogView.findViewById(R.id.noteUse);
        final Button update = dialogView.findViewById(R.id.updateNoteButton);
        final Button delete = dialogView.findViewById(R.id.deleteNoteButton);

        final AlertDialog b = dialogBuilder.create();
        b.show();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = editNote.getText().toString().trim();
                if (!TextUtils.isEmpty(comment)) {
                    updateNote(note, comment);
                    b.dismiss();
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteNote(note.getId());
                b.dismiss();
            }
        });
    }


    protected boolean deleteNote(String id){
        if (mService.getCreatorId() == mUser.getId()){              // check if user has permission to commit action
            DatabaseReference temp = FirebaseDatabase.getInstance().getReference("Notes").child(id);
            temp.removeValue();
            Toast.makeText(getApplicationContext(), "Note Deleted", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(), "You do not have permission to delete this note", Toast.LENGTH_LONG).show();
        }
        return true;
    }

    protected void updateNote(Note note, String comment){
        if (note.getCreator() == mUser.getId()){             // check if user has permission to commit action
            DatabaseReference temp = FirebaseDatabase.getInstance().getReference("Notes").child(note.getId());
            Note newNote = new Note(note.getId(),note.getTacheId(),note.getCreator(), comment);
            temp.setValue(newNote);
            Toast.makeText(getApplicationContext(), "Note Updated", Toast.LENGTH_LONG).show();

        }else{
            Toast.makeText(getApplicationContext(), "You do not have permission to edit this note", Toast.LENGTH_LONG).show();
        }
    }











    // Handle adding notes

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String comm = data.getStringExtra("NOTE");
                addNote(comm);
            }
        }
    }


    protected void addNote(){
        Intent intent = new Intent(this, NoteActivity.class);
        startActivityForResult(intent, 1);
    }

    // Ajout note avec valeur comment comme commentaire
    protected void addNote(String comment){
        String id = mDatabaseNotes.push().getKey();
        Note note = new Note(id, mService.getId(), mUser.getId(), comment);
        mDatabaseNotes.child(id).setValue(note);
    }


}
