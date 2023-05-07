local npc = Npc(646, 50)
--TODO: Need to check offi si il est utilisé parce qu'il y a déjà un "Henual" avec l'ID 446 et un autre avec l'ID 655
---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2660)
    end
end

RegisterNPCDef(npc)
