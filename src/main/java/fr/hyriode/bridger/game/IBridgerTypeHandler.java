package fr.hyriode.bridger.game;

import fr.hyriode.bridger.api.Medal;
import fr.hyriode.bridger.api.duration.HyriBridgerDuration;
import fr.hyriode.bridger.api.player.HyriBridgerStats;

public interface IBridgerTypeHandler {

    void sendNewMedal(HyriBridgerStats account, Medal medal);
    void sendNewPB(HyriBridgerStats account, HyriBridgerDuration duration);

    class NormalHandler implements IBridgerTypeHandler {

        @Override
        public void sendNewMedal(HyriBridgerStats account, Medal medal) {
            account.setHighestAcquiredNormalMedal(medal);
            account.update();
        }

        @Override
        public void sendNewPB(HyriBridgerStats account, HyriBridgerDuration duration) {
            account.setPersonalNormalBest(duration);
            account.update();
        }
    }
    class ShortHandler implements IBridgerTypeHandler {
        @Override
        public void sendNewMedal(HyriBridgerStats account, Medal medal) {
            account.setHighestAcquiredShortMedal(medal);
            account.update();
        }

        @Override
        public void sendNewPB(HyriBridgerStats account, HyriBridgerDuration duration) {
            account.setPersonalShortBest(duration);
            account.update();
        }
    }

    class DiagonalHandler implements IBridgerTypeHandler {
        @Override
        public void sendNewMedal(HyriBridgerStats account, Medal medal) {
            account.setHighestAcquiredDiagonalMedal(medal);
            account.update();
        }

        @Override
        public void sendNewPB(HyriBridgerStats account, HyriBridgerDuration duration) {
            account.setPersonalDiagonalBest(duration);
            account.update();
        }
    }
}
