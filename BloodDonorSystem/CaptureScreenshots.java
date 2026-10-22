import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import gui.*;
import model.*;

public class CaptureScreenshots {
    public static void main(String[] args) {
        try {
            File dir = new File("screenshots");
            if (!dir.exists()) dir.mkdirs();

            SwingUtilities.invokeAndWait(() -> {
                try {
                    // 1. Login Frame
                    LoginFrame login = new LoginFrame();
                    login.setSize(480, 620);
                    login.validate();
                    login.doLayout();
                    saveComponent(login, new File(dir, "01_login_screen.png"));
                    login.dispose();

                    // 2. Dashboard
                    User testUser = new User(1, "admin", "admin123");
                    DashboardFrame dash = new DashboardFrame(testUser);
                    dash.setSize(1260, 780);
                    dash.validate();
                    dash.doLayout();
                    saveComponent(dash, new File(dir, "02_dashboard_screen.png"));

                    // 3. Donor Details Dialog
                    Donor sampleDonor = new Donor(
                        "D001", "Arun Kumar", 28, "Male", "O+",
                        "9876543210", "arun@example.com", "12 Anna Nagar", "Chennai",
                        new java.util.Date(), "Available"
                    );
                    DonorDetailsFrame details = new DonorDetailsFrame(dash, sampleDonor);
                    details.setSize(540, 640);
                    details.validate();
                    details.doLayout();
                    saveComponent(details, new File(dir, "07_donor_details_dialog.png"));
                    details.dispose();

                    dash.dispose();

                    System.out.println("Screenshot generation succeeded!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveComponent(Component comp, File file) throws Exception {
        comp.addNotify();
        comp.validate();
        BufferedImage img = new BufferedImage(comp.getWidth(), comp.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        comp.paint(g2);
        g2.dispose();
        ImageIO.write(img, "png", file);
        System.out.println("Saved: " + file.getAbsolutePath());
    }
}
