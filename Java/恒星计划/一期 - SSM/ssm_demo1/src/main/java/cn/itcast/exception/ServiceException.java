package cn.itcast.exception;

public class ServiceException extends RuntimeException {

    private String errMessage;

    public ServiceException(String errMessage){
        this.errMessage=errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }
}
