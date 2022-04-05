package com.shiranaor.GetWork.Callbacks;

// When data passed from firebase, it take time and the program keep running.
// to this Callback will help to return data when he arrived from firebase and then
// to call a function about this data
public interface Callback_ReadData {
    void success(Object data);
    void failed(String message);
}
