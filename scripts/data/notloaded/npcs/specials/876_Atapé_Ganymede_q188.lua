local npc = Npc(876, 9059)
--TODO: Lié à la quête 188
npc.colors = {12571268, 6304805, 7119687}

---@param p Player
---@param answer number

--Questions :
--3771 : Avant d'aller plus loin, je te conseille de lancer quelques sortilèges ou quelques coups sur cet épouvantail. Maîtriser tes pouvoirs te sera fort utile à travers le vaste monde qui t'attend.
--3772 : Essaie donc, tu ne peux pas perdre !
--3773 : Si tu le désires, tu peux recommencer à t'entraîner. L'épouvantail se réincarne sans cesse grâce aux vents d'Incarnam.
--
--Réponses :
--3398 : Je vais tenter de le réduire en paillasson. *Fait craquer ses doigts*
--3400 : Je vais lui voler dans les plumes.
--
--Q.3771 > R.3398 (Donne quete 188)
--
--Q.3772 > R.3400 (Quete en cours)
--
--Q.3773 > (Quete terminer)
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3772, {3400})
    elseif answer == 3400 then p:endDialog()
    end
end

RegisterNPCDef(npc)
