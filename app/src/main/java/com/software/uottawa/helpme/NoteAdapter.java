package com.software.uottawa.helpme;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

/**
 * Created by Anthony B.
 */

public class NoteAdapter extends ArrayAdapter<Note>{
    private Activity context;
    private DatabaseReference mDatabaseNotes;

    List<Note> notes;

    public NoteAdapter(Activity context, List<Note> notes){
        super(context, R.layout.layout_note_adapter, notes);
        this.notes = notes;
        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_note_adapter, null, true);

        TextView creator = listViewItem.findViewById(R.id.poster);
        TextView comment = listViewItem.findViewById(R.id.comment);

        Note note = notes.get(position);
        creator.setText(note.getCreator() + " a poste:");
        comment.setText(note.getComment());
        return listViewItem;


    }
}
