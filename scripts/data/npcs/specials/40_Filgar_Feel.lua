local npc = Npc(40, 9025)
--TODO: NPC Temple Éca > Check dialogs offi en étant Xélor, quand on est éca et qu'on choisit la réponse 164 cela ouvre le dialog 199 qui a la réponse 165 puis
--TODO: en fonction de si on a un trèfle ou non cela lance le dialog 201 ou 202, à check avec un éca qui a un trèfle pour sniff la fin des dialogs
npc.sales = {
    {item=1324},
    {item=494}
}
---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(93, {164})
    elseif answer == 164 then p:ask(200)
    end
end

RegisterNPCDef(npc)
