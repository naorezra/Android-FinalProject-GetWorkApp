package com.shiranaor.GetWork.Callbacks;


// When process happens on firebase, it take time and the program keep running.
// to this Callback will help to return data when he arrived from firebase and then
// to call a function about this data
public interface Callback_Success {
    void success(String message);
    void failed(String message);
}
