    package com.example.contactlistapp


    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.Button
    import android.widget.EditText
    import android.widget.TextView
    import android.widget.Toast
    import androidx.appcompat.app.AlertDialog
    import androidx.constraintlayout.utils.widget.ImageFilterView
    import androidx.recyclerview.widget.RecyclerView
    import com.google.firebase.firestore.DocumentSnapshot
    import com.google.firebase.firestore.FirebaseFirestore
    import com.google.firebase.firestore.ktx.firestore
    import com.google.firebase.ktx.Firebase



    class Notesadapter(val items:ArrayList<usermodel>,private val listener :NotesItemClicked) : RecyclerView.Adapter<NotesViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
            val view= LayoutInflater.from(parent.context).inflate(R.layout.item_contact,parent,false)
            val btn= view.findViewById<ImageFilterView>(R.id.deletebutton)
            val editbtn = view.findViewById<ImageFilterView>(R.id.editbutton)
            val viewHolder= NotesViewHolder(view)
    //        view.setOnClickListener{
    //            listner.onItemClicked(items[viewHolder.adapterPosition])
    //        }
            btn.setOnClickListener {
                // listner.onItemClicked(items[viewHolder.adapterPosition])
                val documentIdToDelete = items[viewHolder.adapterPosition].firestoreId
                var fstore = Firebase.firestore
                fstore = FirebaseFirestore.getInstance()
                fstore.collection("note")
                    .document(documentIdToDelete)
                    .delete()
                    .addOnSuccessListener {
                        items.removeAt(viewHolder.adapterPosition)
                        notifyDataSetChanged()

                    }

                    .addOnFailureListener { exception ->
                        // Handle delete failure
                        //  android.widget.Toast.makeText(this, "Failed to delete", android.widget.Toast.LENGTH_LONG).show()
                    }

            }
            editbtn.setOnClickListener {
    //            val documentIdtoedit = items[viewHolder.adapterPosition].firestoreId
    //            var fstore = Firebase.firestore
    //            fstore.collection("note")
    //            Toast.makeText(parent.context,"Edit button clicked ",Toast.LENGTH_LONG).show()
//                listener.onItemClicked(items[viewHolder.adapterPosition])
                val item = items[viewHolder.adapterPosition]
//                Toast.makeText(parent.context,"edit button clicked ", Toast.LENGTH_LONG).show()
                val dialogView = LayoutInflater.from(parent.context).inflate(R.layout.dialog_edit, null)
                val editNameField = dialogView.findViewById<EditText>(R.id.editName)
                val editNumberField = dialogView.findViewById<EditText>(R.id.editNumber)

                editNameField.setText(item.nametext)
                editNumberField.setText(item.numbertext)

                AlertDialog.Builder(parent.context)
                    .setTitle("Edit Contact")
                    .setView(dialogView)
                    .setPositiveButton("Save") { dialog, _ ->
                        val newName = editNameField.text.toString()
                        val newNumber = editNumberField.text.toString()

                        updateContactInFirestore(item.firestoreId, newName, newNumber)
                        val updatedItem = item.copy(nametext = newName, numbertext = newNumber)
                        val position = items.indexOf(item)
                        if (position != -1) {
                            items[position] = updatedItem
                            notifyItemChanged(position)
                        }

                    }
                    .setNegativeButton("Cancel", null)
                    .show()

            }

            return viewHolder
        }

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
            val currentitem=items[position]
            holder.titlename.text=items[position].nametext
            holder.titlenumber.text = items[position].numbertext


        }


        private fun updateContactInFirestore(documentId: String, newName: String, newNumber: String) {

            val firestore = FirebaseFirestore.getInstance()
            val updates = hashMapOf(
                "nametext" to newName,
                "numbertext" to newNumber
            )

            firestore.collection("note")
                .document(documentId)
                .update(updates as Map<String, Any>)
                .addOnSuccessListener {
//                    Toast.makeText(this, "Contact updated successfully", Toast.LENGTH_SHORT).show()
                    notifyDataSetChanged()

                }
                .addOnFailureListener { exception ->
//                    Toast.makeText(this, "Failed to update contact: ${exception.message}", Toast.LENGTH_SHORT).show()
                }

        }

    }

    class NotesViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){


        val titlename=itemView.findViewById<TextView>(R.id.text)
        val titlenumber = itemView.findViewById<TextView>(R.id.text_contact)

    }
    interface NotesItemClicked{
        fun onItemClicked(item: usermodel)
    }
