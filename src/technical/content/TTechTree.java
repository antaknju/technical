package technical.content;

import static mindustry.content.TechTree.*;

// import mindustry.game.Objectives.*;
// import arc.struct.Seq;
// import mindustry.content.Items;
// import mindustry.content.Liquids;
// import mindustry.game.Objectives.SectorComplete;

public class TTechTree 
{
    public static void load() 
    {
        TPlanets.tertaris.techTree = nodeRoot("trabatros", TBlocks.basic_core, () -> {
            nodeProduce(TItems.metallurgy_xp, () -> {

            });

            node(TCustom.crudeMetallurgy, () -> {
                
            });
        });
    }       
}
//AourusPlanets.aourus.techTree = nodeRoot("aourus", AourusBlocks.glass_core, true, () -> {

        //     node (AourusMisc.sulphur_missile, () -> {

        //     });

        //     node (AourusBlocks.magnet_core, () -> {
                
        //     });

        //     node(AourusBlocks.water_extractor, Seq.with(new Research(AourusBlocks.lead_duct)), () -> {
        //         node(AourusBlocks.glass_conduit, () -> {
        //             node(AourusBlocks.glass_conduit_bridge, () -> {
                        
        //             });
        //             node(AourusBlocks.glass_conduit_router, () -> {
                            
        //             });
        //             node (AourusBlocks.hydrogenator, () -> {
                        
        //             });
        //         });
        //     });

        //     node (AourusBlocks.sulphur_burner, () -> {
        //         node (AourusBlocks.torch, Seq.with(new Research(AourusBlocks.electrical_beam_node)), () -> {
                    
        //         });
        //         node (AourusBlocks.electrical_beam_node, Seq.with(new Research(AourusBlocks.burst_drill)), () -> {
                    
        //         });

        //         node (AourusBlocks.sulphur_fluid_generator, () -> {
                        
        //         });
        //     });

        //     node(AourusBlocks.burst_drill, Seq.with(new Research(AourusBlocks.sulphur_burner)), () -> {
        //         node(AourusBlocks.sulphur_extractor, Seq.with(new OnSector(AourusSectors.against)), () -> {
                
        //         });
        //     });

        //     node(AourusBlocks.water_turret, Seq.with(new Research(AourusBlocks.water_extractor)), () -> {
        //         node(AourusBlocks.alphus, () -> {
        //             node(AourusBlocks.coiler, () -> {

        //             });
        //         });
        //         node(AourusBlocks.silicon_wall, () -> {
        //             node(AourusBlocks.large_silicon_wall, () -> {
                        
        //             });
        //         });
        //     });

        //     node(AourusBlocks.lead_duct, Seq.with(new Research(AourusBlocks.torch)), () -> {
        //         node(AourusBlocks.lead_duct_bridge, () -> {
                        
        //         });
        //         node(AourusBlocks.lead_duct_router, () -> {
                            
        //         });
        //     });

        //     node(AourusBlocks.silicon_smelter, () -> {
        //         node(AourusBlocks.glass_furnace, () -> { // , Seq.with(new SectorComplete(AourusSectors.sturdy_hills))
        //         });
        //     });

        //     nodeProduce (Items.lead, () -> {
        //         nodeProduce (AourusItems.sulphur, () -> {
        //             nodeProduce (Items.silicon, () -> {
        //                 nodeProduce (Liquids.slag, () -> {
                        
        //                 });
        //             });
        //         });
        //         nodeProduce (Items.sand, () -> {
        //             nodeProduce (AourusItems.glass, () -> {
                        
        //             });
        //         });
        //         nodeProduce (Items.graphite, () -> {
                    
        //         });
        //         nodeProduce (Liquids.water, () -> {
        //             nodeProduce (Liquids.hydrogen, () -> {
                        
        //             });
        //         });
        //     });

        //     node(TUnits.aqarus, () -> {
        //         node(AourusBlocks.sulphur_unit_factory, () -> {
        //             node(TUnits.boomer, () -> {
        //                 node(TUnits.tyrant, () -> {
                            
        //                 });
        //             });
        //             node(TUnits.shrine, () -> { // , Seq.with(new SectorComplete(AourusSectors.deep_forward))
        //                 node(TUnits.flex, () -> {
                            
        //                 });
        //             });
        //             node(TUnits.sulphury, () -> { // , Seq.with(new SectorComplete(AourusSectors.deep_forward))
        //                 node(TUnits.maltorion, () -> {

        //                 });
        //             });
        //             node(TUnits.tantaros, () -> {
                            
        //             });
        //         });
        //     });

        //     node(AourusStatuses.sulphured, () -> {
                
        //     });

        //     node (AourusSectors.sector0, () -> {
        //         // node (AourusSectors.deep_forward, () -> {

        //         // });
        //         // node (AourusSectors.sturdy_hills, () -> {

        //         // });
        //         node (AourusSectors.against, () -> {
        //             node (AourusSectors.enclosed, () -> {

        //             });
        //         });
        //     });
        // });