package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        User user1=userRepository.findById(subscriptionEntryDto.getUserId()).get();
        //Save The subscription Object into the Db and return the total Amount that user has to pay
        Subscription subscription=new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        subscription.setStartSubscriptionDate(new Date());
        int  totalAmt=0;
        SubscriptionType st=subscriptionEntryDto.getSubscriptionType();
        if(st.equals(SubscriptionType.BASIC))
        {
            totalAmt+= 500+(200*subscriptionEntryDto.getNoOfScreensRequired());
        }
        else if (st.equals(SubscriptionType.PRO))
        {
            totalAmt+= 800+ (250*subscriptionEntryDto.getNoOfScreensRequired());
        }
        else
        {
            totalAmt+= 1000+(350*subscriptionEntryDto.getNoOfScreensRequired());
        }
        subscription.setTotalAmountPaid(totalAmt);
        subscription.setUser(user1);
        user1.setSubscription(subscription);
        subscriptionRepository.save(subscription);
        return totalAmt;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        User user1=userRepository.findById(userId).get();
        Subscription subscription=user1.getSubscription();
        if(subscription.getSubscriptionType().equals(SubscriptionType.ELITE))
        {
            throw new Exception("Already the best Subscription");
        }
        int prevAmt=subscription.getTotalAmountPaid();
        int newAmt=0;
        if(subscription.getSubscriptionType().equals(SubscriptionType.BASIC))
        {
            subscription.setSubscriptionType(SubscriptionType.PRO);
            newAmt=800+(250*subscription.getNoOfScreensSubscribed());
        }
        else
        {
            subscription.setSubscriptionType(SubscriptionType.ELITE);
            newAmt=1000+(350*subscription.getNoOfScreensSubscribed());
        }
        subscription.setTotalAmountPaid(newAmt);
        user1.setSubscription(subscription);
        subscriptionRepository.save(subscription);
        return newAmt-prevAmt;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        List<Subscription>subscriptionList=subscriptionRepository.findAll();
        Integer totalRevenue = 0;
        for(Subscription subscription : subscriptionList){
            totalRevenue += subscription.getTotalAmountPaid();
        }
        return totalRevenue;
    }

}
