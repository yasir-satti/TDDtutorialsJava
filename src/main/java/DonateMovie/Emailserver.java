package DonateMovie;

public interface Emailserver {
    void sendEmail(String template,
                   String distributionList,
                   String[] params);
}
