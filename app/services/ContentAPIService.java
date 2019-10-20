package services;

public class ContentAPIService {
    // This is an empty service, we'll implement some code soon !


    // function to return the random number between a range
    public int getRandm(int min,int max){
        int rnd = (int)((Math.random()*((max-min)+1))+min);
        return rnd;
    }
}
