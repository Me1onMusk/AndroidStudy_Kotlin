package com.example.chatting.chatdetail

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatting.Key
import com.example.chatting.Key.Companion.DB_CHATS
import com.example.chatting.Key.Companion.DB_CHAT_ROOMS
import com.example.chatting.Key.Companion.DB_USERS
import com.example.chatting.databinding.ActivityChatdetailBinding
import com.example.chatting.userlist.UserItem
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.database

class ChatActivity : AppCompatActivity() {
    companion object {
        const val TAG = "ChatActivity"
        const val EXTRA_CHAT_ROOM_ID = "CHAT_ROOM_ID"
        const val EXTRA_OTHER_USER_ID = "OTHER_USER_ID"
    }

    private lateinit var binding: ActivityChatdetailBinding

    private var chatRoomID: String = ""
    private var otherUserID: String = ""
    private var myUserID: String = ""
    private var myUserName: String = ""

    private val chatItemList = mutableListOf<ChatItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatdetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chatRoomID = intent.getStringExtra(EXTRA_CHAT_ROOM_ID) ?: return
        otherUserID = intent.getStringExtra(EXTRA_OTHER_USER_ID) ?: return
        myUserID = Firebase.auth.currentUser?.uid ?: ""
        val chatAdapter = ChatAdapter()

        Firebase.database.reference.child(DB_USERS).child(myUserID).get()
            .addOnSuccessListener {
                val myUserItem = it.getValue(UserItem::class.java)
                myUserName = myUserItem?.userName ?: ""
            }
        Firebase.database.reference.child(DB_USERS).child(otherUserID).get().addOnSuccessListener {
            val otherUserItem = it.getValue(UserItem::class.java)
            chatAdapter.otherUserItem = otherUserItem
        }

        // 채팅(Chats) 가져오기 //
        Firebase.database.reference.child(DB_CHATS).child(chatRoomID)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val chatItem = snapshot.getValue(ChatItem::class.java)
                    chatItem ?: return

                    chatItemList.add(chatItem)  //채팅 리스트
                    chatAdapter.submitList(chatItemList.toMutableList())  //리스트 복사 (갱신)
                }
                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}  //채팅 변경

                override fun onChildRemoved(snapshot: DataSnapshot) {}  //채팅 삭제

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onCancelled(error: DatabaseError) {}  //채팅 취소
            })

        // 채팅 목록 RecyclerView 설정 //
        binding.chatDetailRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = chatAdapter
        }

        // 초기에는 버튼을 비활성화
        binding.sendButton.isEnabled = false

        // EditText에 TextWatcher 추가
        binding.messageEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.sendButton.isEnabled = !s.isNullOrEmpty()  //EditText가 비어 있는지 확인하여 버튼 상태 설정
            }

            override fun afterTextChanged(s: Editable?) {}

        })

        // 채팅 메시지 전송 버튼 //
        binding.sendButton.setOnClickListener {
            val message = binding.messageEditText.text.toString()
            val newChatItem = ChatItem(  //채팅 데이터 생성
                message = message,
                userID = myUserID
            )
            Firebase.database.reference.child(DB_CHATS).child(chatRoomID).push().apply {
                newChatItem.chatID = key
                setValue(newChatItem)
            }

            val updates: MutableMap<String, Any> = hashMapOf (
                "$DB_CHAT_ROOMS/$myUserID/$otherUserID/lastMessage" to message,
                "$DB_CHAT_ROOMS/$otherUserID/$myUserID/lastMessage" to message,
                "$DB_CHAT_ROOMS/$otherUserID/$myUserID/chatRoomID" to chatRoomID,
                "$DB_CHAT_ROOMS/$otherUserID/$myUserID/otherUserID" to myUserID,
                "$DB_CHAT_ROOMS/$otherUserID/$myUserID/otherUserName" to myUserName,
            )

            Firebase.database.reference.updateChildren(updates)

            binding.messageEditText.text.clear()
        }
    }

}